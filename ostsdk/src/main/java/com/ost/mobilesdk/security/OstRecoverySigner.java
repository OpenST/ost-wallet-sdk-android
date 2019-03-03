package com.ost.mobilesdk.security;

import android.text.TextUtils;

import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.DelayedRecoveryModule;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.workflows.errors.OstError;
import static com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;


public class OstRecoverySigner {
    private final String mUserId;
    public OstRecoverySigner(String userId) {
        mUserId = userId;
    }

    public SignedRestRecoveryStruct getResetRecoveryOwnerSignature(String passphrasePrefix, String userPassphrase, String scriptSalt, String newUserPassphrase) {
        OstUser user = OstUser.getById(mUserId);
        String recoveryOwnerAddress = user.getRecoveryOwnerAddress();
        String recoveryContractAddress = user.getRecoveryAddress();
        if ( TextUtils.isEmpty(recoveryOwnerAddress) || TextUtils.isEmpty(recoveryContractAddress) ) {
            OstError error = new OstError("km_rs_grrows_1", ErrorCode.RECOVERY_PASSPHRASE_OWNER_NOT_SET);
            throw error;
        }

        // Sanity check validity of new user passphrase.
        if ( !areRecoveryPassphraseInputsValid(passphrasePrefix, userPassphrase, scriptSalt) ) {
            OstError error = new OstError("km_rs_grrows_2", ErrorCode.INVALID_NEW_USER_PASSPHRASE);
            throw error;
        }

        // Generate new recovery-owner-key
        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        ECKeyPair newKeyPair = ikm.createRecoveryECKey(passphrasePrefix,newUserPassphrase,scriptSalt);
        String newRecoveryOwnerAddress = Credentials.create(newKeyPair).getAddress();
        newKeyPair = null;

        //Generate EIP-712 TypedData Sign Hash
        JSONObject eip712TypedData = new DelayedRecoveryModule().resetRecoveryOwnerData(recoveryOwnerAddress,
                newRecoveryOwnerAddress, recoveryContractAddress);
        if (null == eip712TypedData) {
            OstError error = new OstError("km_rs_grrows_3", ErrorCode.EIP712_FAILED);
            throw error;
        }
        String eip712Hash = getEIP712SignHash(eip712TypedData);

        // Check if recovery is locked.
        if ( !ikm.isUserPassphraseValidationAllowed() ) {
            OstError error = new OstError("km_rs_grppws_4", ErrorCode.MAX_PASSPHRASE_VERIFICATION_LIMIT_REACHED);
            throw error;
        }

        // Get the signature.
        String signature = ikm.signDataWithRecoveryKey(passphrasePrefix, userPassphrase, scriptSalt, eip712Hash);
        if ( null == signature ) {
            OstError error = new OstError("km_rs_grrows_5", ErrorCode.INVALID_USER_PASSPHRASE);
            throw error;
        }

        return new SignedRestRecoveryStruct(newRecoveryOwnerAddress,recoveryOwnerAddress,recoveryContractAddress, eip712TypedData, signature);
    }


    private String getEIP712SignHash(JSONObject typedData) {
        try {
            return new EIP712(typedData).toEIP712TransactionHash();
        } catch (Exception e) {
            OstError error = new OstError("km_rs_grrows_4", ErrorCode.EIP712_FAILED);
            throw error;
        }
    }

    boolean areRecoveryPassphraseInputsValid(String passphrasePrefix, String userPassphrase, String scriptSalt) {
        return InternalKeyManager.sanityCheckRecoveryPassphraseInputsValidity(passphrasePrefix, userPassphrase, scriptSalt);
    }
}
