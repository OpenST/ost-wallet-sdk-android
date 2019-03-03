package com.ost.mobilesdk.security;

import android.util.Log;
import com.ost.mobilesdk.workflows.OstUserPinInfoHolder;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

public class OstKeyManager {
    private static final String TAG = "OstKeyManager";


    private final String mUserId;
    private KeyMetaStruct mKeyMetaStruct;

    public OstKeyManager(String userId) {
        this.mUserId = userId;
        mKeyMetaStruct = InternalKeyManager2.getKeyMataStruct(userId);
        if (null == mKeyMetaStruct ) {

            //Create new KeyManagerInstance so that keys are created.
            InternalKeyManager2 ikm = new InternalKeyManager2(userId);

            //Fetch KeyMataStruct.
            mKeyMetaStruct = InternalKeyManager2.getKeyMataStruct(userId);
            ikm = null;
        }
    }

    public String getApiKeyAddress() {
        return mKeyMetaStruct.getApiAddress();
    }

    public byte[] getMnemonics() {
        InternalKeyManager2 ikm = new InternalKeyManager2(mUserId);

        String deviceAddress = getDeviceAddress();
        byte[] mnemonics = ikm.getMnemonics(deviceAddress);
        ikm = null;
        return mnemonics;
    }

    public String createSessionKey() {
        InternalKeyManager2 ikm = new InternalKeyManager2(mUserId);
        String address = ikm.createSessionKey();
        ikm = null;

        if ( null == address ) {
            throw new OstError("km_okm_csk_1", ErrorCode.SESSION_KEY_GENERATION_FAILED);
        }

        return address;
    }

    public String getDeviceAddress() {
        return mKeyMetaStruct.getDeviceAddress();
    }



    private int cnt = 0;
    public boolean validatePin(OstUserPinInfoHolder pinInfoHolder) {
        cnt++;
        Log.i(TAG,"validatePin called " + cnt + " time(s).");
        //Temp code.
        UserPassphrase passphrase = new UserPassphrase(mUserId, pinInfoHolder.getUserPassphrase(), pinInfoHolder.getPassphrasePrefix());
        return new OstRecoveryManager(mUserId).validatePassphrase(passphrase);

    }

}