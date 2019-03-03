package com.ost.mobilesdk.security;

import android.text.TextUtils;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
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

    public boolean validatePassphrase(UserPassphrase passphrase) {
        InternalKeyManager ikm = null;
        try {
            ikm = new InternalKeyManager(userId);
            if ( !ikm.isUserPassphraseValidationAllowed() ) {
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

    //region - SCrypt salt from Kit.
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
    //endregion

    public SignedResetRecoveryStruct getResetRecoveryOwnerSignature(UserPassphrase currentPassphrase, UserPassphrase newUserPassphrase) {
        OstUser user = OstUser.getById(userId);
        InternalKeyManager ikm = new InternalKeyManager(userId);
        // Check if recovery is locked.
        if (!ikm.isUserPassphraseValidationAllowed()) {
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
        if (currentPassphrase.isWiped() || newUserPassphrase.isWiped()) {
            currentPassphrase.wipe();
            newUserPassphrase.wipe();
            throw new OstError("km_rs_grrows_3", ErrorCode.INVALID_NEW_USER_PASSPHRASE);
        }


        //Get salt from kit.
        byte[] salt = null;
        byte[] saltForNewRecoveryOwner = null;

        String newRecoveryOwnerAddress = null;
        String signature = null;
        try {

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
        }
    }


    private String getEIP712SignHash(JSONObject typedData) {
        try {
            return new EIP712(typedData).toEIP712TransactionHash();
        } catch (Exception e) {
            OstError error = new OstError("km_rs_grrows_4", ErrorCode.EIP712_FAILED);
            throw error;
        }
    }

}
