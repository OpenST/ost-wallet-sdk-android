package com.ost.mobilesdk.ecKeyInteracts;

import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.network.OstApiError;
import com.ost.mobilesdk.network.OstHttpRequestClient;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;

import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

public class OstApiSigner implements OstHttpRequestClient.ApiSigner {

    String mUserId;
    public OstApiSigner(String userId) {
        mUserId = userId;
    }

    /**
     * Generates signature for HTTP Api calls (ETH Personal Sign).
     * @param dataToSign - byte[] to sign.
     * @return
     */
    @Override
    public String sign(byte[] dataToSign) {
        InternalKeyManager ikm = null;
        try {
            ikm = new InternalKeyManager(mUserId);
            return ikm.signBytesWithApiSigner(dataToSign);
        } finally {
            ikm = null;
        }
    }

    public void apiSignerUnauthorized(OstApiError error) {
        if ( null == error || !error.isApiSignerUnauthorized()) {
            return;
        }
        try {
            KeyMetaStruct meta = InternalKeyManager.getKeyMataStruct(mUserId);
            String currentDeviceAddress = meta.getDeviceAddress();
            if ( null != currentDeviceAddress) {
                OstDevice device = OstDevice.getById(currentDeviceAddress);
                if ( null != device && device.canBeRegistered() ) {
                    //Ignore this call for devices with status Created.
                    return;
                }
            }
            InternalKeyManager.apiSignerUnauthorized(mUserId);
        } catch (Throwable th) {
            OstError caughtError;
            if ( th instanceof OstError ) {
                caughtError = (OstError) th;
            } else {
                caughtError = new OstError("km_as_asu_1", OstErrors.ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            }
            throw caughtError;
        }
    }

}