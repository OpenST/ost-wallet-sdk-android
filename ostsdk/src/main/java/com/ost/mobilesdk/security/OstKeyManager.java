package com.ost.mobilesdk.security;

import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.workflows.OstUserPinInfoHolder;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

public class OstKeyManager {
    private static final String TAG = "OstKeyManager";


    private final String mUserId;
    private KeyMetaStruct mKeyMetaStruct;

    public OstKeyManager(String userId) {
        this.mUserId = userId;
        mKeyMetaStruct = InternalKeyManager.getKeyMataStruct(userId);
        if (null == mKeyMetaStruct ) {

            //Create new KeyManagerInstance so that keys are created.
            InternalKeyManager ikm = new InternalKeyManager(userId);

            //Fetch KeyMataStruct.
            mKeyMetaStruct = InternalKeyManager.getKeyMataStruct(userId);
            ikm = null;
        }
    }

    static String signUsingSeed(byte[] seed, String eip712Hash) {
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genHDKey(seed);
        return sign(eip712Hash, ecKeyPair);
    }

    public static String sign(String eip712Hash, ECKeyPair ecKeyPair) {
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(eip712Hash), ecKeyPair, false);
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x", (signatureData.getV()));
    }

    public String getApiKeyAddress() {
        return mKeyMetaStruct.getApiAddress();
    }

    String[] getMnemonics(String address) {
        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        String[] mnemonics = ikm.getMnemonics(address);
        ikm = null;
        return mnemonics;
    }

    public String createSessionKey() {
        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        String address = ikm.createSessionKey();
        ikm = null;

        if ( null == address ) {
            OstError error = new OstError("km_okm_csk_1", ErrorCode.SESSION_KEY_GENERATION_FAILED);
            throw error;
        }

        return address;
    }

//    public boolean hasAddress(String address) {
//        return mKeyMetaStruct.hasAddress( address );
//    }

    public String getDeviceAddress() {
        return mKeyMetaStruct.getDeviceAddress();
    }

    public String[] getMnemonics() {
        String deviceAddress = getDeviceAddress();
        return getMnemonics(deviceAddress);
    }

    public boolean validatePin(String pin, String appSalt, String kitSalt) {
        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        if ( !ikm.isUserPassphraseValidationAllowed() ) {
            return false;
        }
        boolean isValid = ikm.validateUserPassphrase(appSalt, pin, kitSalt);
        ikm = null;
        return isValid;
    }

    public boolean validatePin(OstUserPinInfoHolder pinInfoHolder) {
        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        if ( !ikm.isUserPassphraseValidationAllowed() ) {
            OstError error = new OstError("km_okm_vpin_1", ErrorCode.MAX_PASSPHRASE_VERIFICATION_LIMIT_REACHED);
            throw error;
        }
        boolean isValid = ikm.validateUserPassphrase(pinInfoHolder.getPassphrasePrefix(), pinInfoHolder.getUserPassphrase(), pinInfoHolder.getScriptSalt());
        ikm = null;
        return isValid;

    }

    public String getRecoveryKeyAddressUsing(String appSalt, String pin, String kitSalt) {
        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        String address = ikm.getRecoveryKeyAddressUsing(appSalt,pin,kitSalt);
        ikm = null;
        if ( null == address ) {
            OstError error = new OstError("km_okm_csk_1", ErrorCode.RECOVERY_KEY_GENERATION_FAILED);
            throw error;
        }
        return address;
    }

    public String signUsingSessionKey(String a, String b) {
        OstError error = new OstError("km_okm_susk_1", ErrorCode.DEPRECATED);
        throw error;
    }

}