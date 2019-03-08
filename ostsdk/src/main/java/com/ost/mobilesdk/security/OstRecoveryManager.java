package com.ost.mobilesdk.security;

import android.text.TextUtils;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.structs.SignedRecoverOperationStruct;
import com.ost.mobilesdk.security.structs.SignedResetRecoveryStruct;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.utils.DelayedRecoveryModule;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static org.web3j.compat.Compat.UTF_8;

public class OstRecoveryManager {
    private static String TAG = "OstRecoveryManager";
    private static final String SALT = "salt";
    private static final String SCRYPT_SALT = "scrypt_salt";

    private String userId;
    private OstApiClient apiClient;
    public OstRecoveryManager(String userId) {
        this.userId = userId;
        apiClient = new OstApiClient(userId);
    }

    private OstUser ostUser() {
        return OstUser.getById(userId);
    }

    public String getRecoveryAddressFor(UserPassphrase passphrase) {
        InternalKeyManager ikm = null;
        try {
            if ( ostUser().isActivated() ) {
                throw new OstError("km_orm_gra_1", ErrorCode.USER_ALREADY_ACTIVATED);
            } else if ( ostUser().isActivating() ) {
                throw new OstError("km_orm_gra_2", ErrorCode.USER_ACTIVATING);
            }

            ikm = new InternalKeyManager(userId);
            return ikm.getRecoveryAddress(passphrase, getSalt());
        } finally {
            ikm = null;
        }
    }

    /**
     * Validate User's passphrase
     * @param passphrase - User's passphrase.
     * @return true if passphrase can prove it's validity.
     */
    public boolean validatePassphrase(UserPassphrase passphrase) {
        InternalKeyManager ikm = null;
        try {
            ikm = new InternalKeyManager(userId);
            if ( ikm.isUserPassphraseValidationLocked() ) {
                throw new OstError("km_orm_vp_2", ErrorCode.USER_PASSPHRASE_VALIDATION_LOCKED);
            }

            if ( !ostUser().getId().equals(passphrase.getUserId()) ) {
                ikm.userPassphraseInvalidated();
                throw new OstError("km_orm_vp_1", ErrorCode.INVALID_USER_PASSPHRASE);
            }

            if ( !ostUser().isActivated() ) {
                ikm.userPassphraseInvalidated();
                throw new OstError("km_orm_vp_2", ErrorCode.USER_NOT_ACTIVATED);
            }

            return ikm.validateUserPassphrase(passphrase, getSalt());
        } finally {
            ikm = null;
            passphrase.wipe();
        }
    }

    /**
     * Generates signature required to reset user's passphrase.
     * @param currentPassphrase - User's current passphrase
     * @param newUserPassphrase - User's new passphrase
     * @return EIP-712 Signature that shall be used to reset the User's passphrase.
     */
    public SignedResetRecoveryStruct getResetRecoveryOwnerSignature(UserPassphrase currentPassphrase, UserPassphrase newUserPassphrase) {
        //Get salt from kit.
        byte[] salt = null;
        byte[] saltForNewRecoveryOwner = null;
        InternalKeyManager ikm = null;
        String newRecoveryOwnerAddress = null;
        String signature = null;
        try {
            forceSyncUser();
            OstUser user = OstUser.getById(userId);
            ikm = new InternalKeyManager(userId);
            // Check if recovery is locked.
            if ( ikm.isUserPassphraseValidationLocked() ) {
                currentPassphrase.wipe();
                newUserPassphrase.wipe();
                throw new OstError("km_rs_grppws_1", ErrorCode.MAX_PASSPHRASE_VERIFICATION_LIMIT_REACHED);
            }


            String recoveryOwnerAddress = user.getRecoveryOwnerAddress();
            String recoveryContractAddress = user.getRecoveryAddress();
            if (TextUtils.isEmpty(recoveryOwnerAddress) || TextUtils.isEmpty(recoveryContractAddress)) {
                currentPassphrase.wipe();
                newUserPassphrase.wipe();
                throw new OstError("km_rm_grrows_2", ErrorCode.RECOVERY_PASSPHRASE_OWNER_NOT_SET);
            }

            // Sanity check validity of new user passphrase.
            if (currentPassphrase.isWiped() ) {
                ikm.userPassphraseInvalidated();
                throw new OstError("km_rs_grrows_3", ErrorCode.INVALID_USER_PASSPHRASE);
            }
            if (newUserPassphrase.isWiped()) {
                currentPassphrase.wipe();
                throw new OstError("km_rs_grrows_3", ErrorCode.INVALID_NEW_USER_PASSPHRASE);
            }
            salt = getSalt();
            saltForNewRecoveryOwner = salt.clone();
            newRecoveryOwnerAddress = ikm.getRecoveryAddress(newUserPassphrase, saltForNewRecoveryOwner);

            // Create Data

            JSONObject eip712TypedData = new DelayedRecoveryModule().resetRecoveryOwnerData(recoveryOwnerAddress,
                    newRecoveryOwnerAddress, recoveryContractAddress);

            if (null == eip712TypedData) {
                throw new OstError("km_rs_grrows_4", ErrorCode.EIP712_FAILED);
            }
            String eip712Hash = getEIP712SignHash(eip712TypedData);

            //Set recovery contract address.
            SignedResetRecoveryStruct dataHolder = new SignedResetRecoveryStruct(newRecoveryOwnerAddress);
            dataHolder.setRecoveryContractAddress(recoveryContractAddress);
            dataHolder.setRecoveryOwnerAddress(recoveryOwnerAddress);
            dataHolder.setTypedData(eip712TypedData);
            dataHolder.setMessageHash(eip712Hash);

            //Sign the message hash
            signature = ikm.signDataWithRecoveryKey(eip712Hash, currentPassphrase, salt);
            if ( null == signature ) {
                throw new OstError("km_rs_grrows_5", ErrorCode.INVALID_USER_PASSPHRASE);
            }
            dataHolder.setSignature(signature);
            return dataHolder;

        } finally {
            if ( null == newRecoveryOwnerAddress && null != saltForNewRecoveryOwner) {
                CommonUtils.clearBytes(saltForNewRecoveryOwner);
            }
            if ( null == signature ) {
                CommonUtils.clearBytes(salt);
            }
            currentPassphrase.wipe();
            newUserPassphrase.wipe();
        }
    }

    //region Recovery Operations
    private static String INITIATE_RECOVERY_STRUCT = "InitiateRecoveryStruct";
    private static String ABORT_RECOVERY_STRUCT = "AbortRecoveryStruct";
    public SignedRecoverOperationStruct getRecoverDeviceSignature(UserPassphrase passphrase, String deviceAddressToRevoke) {
        byte[] salt = null;
        String signature = null;
        InternalKeyManager ikm = null;
        SignedRecoverOperationStruct dataHolder;
        try {
            forceSyncUser();

            OstUser user = ostUser();
            ikm = new InternalKeyManager(userId);

            // Check if recovery is locked.
            if ( ikm.isUserPassphraseValidationLocked() ) {
                throw new OstError("km_rs_grds_1", ErrorCode.MAX_PASSPHRASE_VERIFICATION_LIMIT_REACHED);
            }

            // Make user has recovery key
            String recoveryOwnerAddress = user.getRecoveryOwnerAddress();
            String recoveryContractAddress = user.getRecoveryAddress();
            if (TextUtils.isEmpty(recoveryOwnerAddress) || TextUtils.isEmpty(recoveryContractAddress)) {
                throw new OstError("km_rs_grds_2", ErrorCode.RECOVERY_PASSPHRASE_OWNER_NOT_SET);
            }

            OstDevice currentDevice = OstUser.getById(userId).getCurrentDevice();
            if ( !currentDevice.canBeAuthorized() ) {
                throw new OstError("km_rs_grds_3", ErrorCode.DEVICE_CAN_NOT_BE_AUTHORIZED);
            }
            String deviceAddressToBeAuthorized = currentDevice.getAddress();

            OstDevice deviceToRevoke = OstDevice.getById(deviceAddressToRevoke);
            if ( null == deviceToRevoke ) {
                throw new OstError("km_rs_grds_4", ErrorCode.INSUFFICIENT_DATA);
            }

            String linkedAddress = deviceToRevoke.getLinkedAddress();
            if ( null == linkedAddress ) {
                throw new OstError("km_rs_grds_4", ErrorCode.INSUFFICIENT_DATA);
            }

            // Sanity check validity of new user passphrase.
            if ( passphrase.isWiped() ) {
                ikm.userPassphraseInvalidated();
                throw new OstError("km_rs_grds_5", ErrorCode.INVALID_USER_PASSPHRASE);
            }

            try {
                dataHolder = composeRecoveryOperation(INITIATE_RECOVERY_STRUCT, deviceToRevoke, currentDevice);
            } catch (Throwable th) {
                throw new OstError("km_rs_grds_6", ErrorCode.EIP712_FAILED);
            }

            salt = getSalt();
            signature = ikm.signDataWithRecoveryKey(dataHolder.getMessageHash(), passphrase, salt);
            if ( null == signature ) {
                throw new OstError("km_rs_grrows_5", ErrorCode.INVALID_USER_PASSPHRASE);
            }
            dataHolder.setSignature(signature);
            return dataHolder;
        } catch (Throwable th) {
            OstError error;
            if ( th instanceof OstError) {
                error = (OstError) th;
            } else {
                error = new OstError("km_rs_grds_uc_1", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            throw error;
        } finally {
            passphrase.wipe();
            CommonUtils.clearBytes(salt);
        }
    }

    private SignedRecoverOperationStruct composeRecoveryOperation(String primaryType, OstDevice oldDevice, OstDevice newDevice) {

        OstUser user = ostUser();
        String prevOwner = oldDevice.getLinkedAddress();
        String oldOwner = oldDevice.getAddress();
        String newOwner = newDevice.getAddress();
        String recoveryOwnerAddress = user.getRecoveryOwnerAddress();
        String recoveryAddress = user.getRecoveryAddress();
        JSONObject typedData = new DelayedRecoveryModule().getRecoveryOperationTypedData(prevOwner, oldOwner, newOwner, recoveryAddress, primaryType);
        String messageHash = getEIP712SignHash(typedData);

        SignedRecoverOperationStruct dataHolder = new SignedRecoverOperationStruct(primaryType, prevOwner, oldOwner, newOwner);

        dataHolder.setTypedData(typedData);
        dataHolder.setRecoveryOwnerAddress( recoveryOwnerAddress );
        dataHolder.setRecoveryContractAddress( recoveryAddress );
        dataHolder.setMessageHash(messageHash);
        return dataHolder;
    }



    //endregion



    //region - common methods
    private String getEIP712SignHash(JSONObject typedData) {
        try {
            return new EIP712(typedData).toEIP712TransactionHash();
        } catch (Exception e) {
            throw new OstError("km_rs_grrows_4", ErrorCode.EIP712_FAILED);
        }
    }
    //endregion

    //region - Network calls.
    private byte[] getSalt() {
        JSONObject jsonObject = null;
        JSONObject jsonData = null;
        JSONObject jsonSalt = null;
        try {
            jsonObject = apiClient.getSalt();
            jsonData = jsonObject.getJSONObject(OstConstants.RESPONSE_DATA);
            jsonSalt = jsonData.getJSONObject(SALT);
            return jsonSalt.getString(SCRYPT_SALT).getBytes(UTF_8);
        } catch (IOException e) {
            throw new OstError("km_rm_gs_1", ErrorCode.SALT_API_FAILED);
        } catch (JSONException e) {
            throw new OstError("km_rm_gs_2", ErrorCode.SALT_API_FAILED);
        } catch (Throwable th) {
            //Catch everything esle.
            throw new OstError("km_rm_gs_3", ErrorCode.SALT_API_FAILED);
        }
        finally {
            if ( null != jsonSalt && jsonSalt.has(SCRYPT_SALT) ) {
                jsonSalt.remove(SCRYPT_SALT);
            }
        }
    }


    private void forceSyncUser() {
        try {
            apiClient.getUser();
        } catch (IOException e) {
            throw new OstError("km_orm_fsu_1", ErrorCode.GET_USER_API_FAILED);
        }
    }

    private OstDevice getDevice( String deviceAddress ) {
        // Fetch information of device to recover.
        try {
            apiClient.getDevice(deviceAddress);
        } catch (IOException e) {
            throw new OstError("km_orm_gd_1", ErrorCode.GET_DEVICE_API_FAILED);
        }
        OstDevice device = OstDevice.getById(deviceAddress);
        if ( null == device) {
            throw new OstError("km_orm_gd_2", ErrorCode.GET_DEVICE_API_FAILED);
        }
        return device;
    }
    //endregion


}
