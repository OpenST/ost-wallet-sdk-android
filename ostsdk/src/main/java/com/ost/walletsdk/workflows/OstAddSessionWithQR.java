package com.ost.walletsdk.workflows;

import android.text.TextUtils;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.annotations.Nullable;
import com.ost.walletsdk.models.Impls.OstModelFactory;
import com.ost.walletsdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstSession;
import com.ost.walletsdk.network.OstApiClient;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import java.math.BigInteger;
import java.sql.Timestamp;

class OstAddSessionWithQR extends OstAddSession {
    private static final String TAG = "OstAddDeviceWithQR";
    private final String mSessionAddressToBeAdded;

    public OstAddSessionWithQR(String userId, String sessionAddress, String spendingLimit, long expiresAfterInSecs, OstWorkFlowCallback callback) {
        super(userId, spendingLimit, expiresAfterInSecs, callback);
        mSessionAddressToBeAdded = sessionAddress;
    }

    @Override
    String getSessionAddressToAuthorize() {
        return mSessionAddressToBeAdded;
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_SESSION_WITH_QR_CODE;
    }

    static class AddSessionDataDefinitionInstance extends OstDeviceDataDefinitionInstance {
        private static final String TAG = "AddSessionDDInstance";

        public AddSessionDataDefinitionInstance(JSONObject dataObject, String userId, OstWorkFlowCallback callback) {
            super(dataObject, userId, callback);
        }

        @Override
        public OstContextEntity getContextEntity() {
            String sessionAddress = getDeviceAddress();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(OstSession.ADDRESS, sessionAddress);
                jsonObject.put(OstSession.USER_ID, userId);
                jsonObject.put(OstSession.SPENDING_LIMIT, getSpendingLimit());
                jsonObject.put(OstSession.EXPIRATION_HEIGHT, "0");
                jsonObject.put(OstSession.APPROX_EXPIRATION_TIMESTAMP, getExpiryTimestamp());
                jsonObject.put(OstSession.NONCE, "0");
                jsonObject.put(OstSession.UPDATED_TIMESTAMP, System.currentTimeMillis());
                jsonObject.put(OstSession.STATUS, OstSession.CONST_STATUS.CREATED);
                OstSession ostSession = OstSession.parse(jsonObject);
                OstContextEntity contextEntity = new OstContextEntity(ostSession, OstSdk.SESSION);
                return contextEntity;
            } catch (Exception ex) {
                //Can't do anything
            }
            return null;
        }


        @Override
        public void startDataDefinitionFlow() {
            String sessionAddress = getSessionAddress();
            String spendingLimit = getSpendingLimit();
            String stringExpiryTimestamp = getExpiryTimestamp();
            long longExpiryTimestamp = Long.parseLong(stringExpiryTimestamp);
            long currentTimestamp = System.currentTimeMillis() / 1000;
            long expiresAfterInSecs = longExpiryTimestamp - currentTimestamp;

            OstAddSessionWithQR workflow = new OstAddSessionWithQR(userId, sessionAddress, spendingLimit, expiresAfterInSecs, callback);
            workflow.perform();
        }

        @Override
        public void validateDataParams() {
            super.validateDataParams();

            JSONObject sessionData = getSessionData();

            // Validate Session Data
            if ( null == sessionData ) {
                OstError error =  new OstError("wf_asddi_vdp_0", OstErrors.ErrorCode.INVALID_QR_CODE);
                error.addErrorInfo("reason", "Session data (sd) in null");
                throw error;
            }

            String externalSessionAddress = getSessionAddress();
            String apiSignerAddress = getApiSignerAddress();
            String deviceAddress = getDeviceAddress();
            String stringExpiryTimestamp = getExpiryTimestamp();
            String stringSpendingLimit = getSpendingLimit();
            String signature = getSignature();

            // Validate Session Address
            if ( !isValidAddress( externalSessionAddress ) ) {
                OstError error =  new OstError("wf_asddi_vdp_1", OstErrors.ErrorCode.INVALID_SESSION_ADDRESS);
                if ( null == externalSessionAddress ) {
                    error.addErrorInfo("qr_session_address", "null");
                } else {
                    error.addErrorInfo("qr_session_address", externalSessionAddress);
                }
                error.addErrorInfo("userId", userId);
                error.addErrorInfo("reason", "Invalid session address");
                throw error;
            }

            // Validate Api Signer Address
//            if ( !isValidAddress( apiSignerAddress ) ) {
//                OstError error =  new OstError("wf_asddi_vdp_2", OstErrors.ErrorCode.INVALID_API_SIGNER_ADDRESS);
//                if ( null == apiSignerAddress ) {
//                    error.addErrorInfo("qr_api_signer_address", "null");
//                } else {
//                    error.addErrorInfo("qr_api_signer_address", apiSignerAddress);
//                }
//                error.addErrorInfo("userId", userId);
//                error.addErrorInfo("reason", "Invalid api signer address");
//                throw error;
//            }

            // Validate Device Address
            if ( !isValidAddress( deviceAddress ) ) {
                OstError error =  new OstError("wf_asddi_vdp_3", OstErrors.ErrorCode.INVALID_DEVICE_ADDRESS);
                if ( null == deviceAddress ) {
                    error.addErrorInfo("qr_device_address", "null");
                } else {
                    error.addErrorInfo("qr_device_address", deviceAddress);
                }
                error.addErrorInfo("userId", userId);
                error.addErrorInfo("reason", "Invalid session address");
                throw error;
            }

            // Validate Session Expiry timestamp.

            if ( null == stringExpiryTimestamp ) {
                OstError error =  new OstError("wf_asddi_vdp_4", OstErrors.ErrorCode.INVALID_SESSION_EXPIRY_TIME);
                error.addErrorInfo("qr_session_expiry_timestamp", "null" );
                error.addErrorInfo("userId", userId);
                error.addErrorInfo("reason", "Invalid Session expiry timestamp is in past");
                throw error;
            }

            long longExpiryTimestamp;
            Timestamp expiryTimestamp;
            try {
                longExpiryTimestamp = Long.parseLong(stringExpiryTimestamp);
                longExpiryTimestamp = longExpiryTimestamp * 1000;
                expiryTimestamp = new Timestamp( longExpiryTimestamp );
            } catch (Exception ex) {
                OstError error =  new OstError("wf_asddi_vdp_5", OstErrors.ErrorCode.INVALID_SESSION_EXPIRY_TIME);
                error.addErrorInfo("qr_session_expiry_timestamp", stringExpiryTimestamp );
                error.addErrorInfo("userId", userId);
                error.addErrorInfo("reason", "Invalid Session expiry timestamp is in past");
                throw error;
            }

            Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis() );
            if ( !expiryTimestamp.after( currentTimestamp ) ) {
                OstError error =  new OstError("wf_asddi_vdp_6", OstErrors.ErrorCode.INVALID_SESSION_EXPIRY_TIME);
                error.addErrorInfo("qr_session_expiry_timestamp", expiryTimestamp.toString() );
                error.addErrorInfo("current_timestamp", currentTimestamp.toString() );
                error.addErrorInfo("userId", userId);
                error.addErrorInfo("reason", "Session expiry timestamp is in past");
                throw error;
            }

            // Validate spending limit
            try {
                new BigInteger(stringSpendingLimit);
            } catch (Exception ex) {
                throw new OstError("wf_asddi_vdp_7", OstErrors.ErrorCode.INVALID_SESSION_SPENDING_LIMIT);
            }

            // Validate Signature
            if ( null == signature ) {
                OstError error =  new OstError("wf_asddi_vdp_7", OstErrors.ErrorCode.INVALID_QR_CODE);
                error.addErrorInfo("reason", "Invalid QR signature");
                throw error;
            }

            ///TODO - Validate the personal sign.


        }

        @Override
        public void validateApiDependentParams() {
            // Api Client
            OstApiClient apiClient = new OstApiClient(userId);

            // Device and Api Key Validations
            String deviceAddress = getDeviceAddress();
//            String apiKeyAddress = getApiSignerAddress();
            String externalSessionAddress = getSessionAddress();
            apiClient.getDevice(deviceAddress);

            // Device Validations
            OstDevice ostDevice = OstDevice.getById(deviceAddress);
            if (null == ostDevice) {
                OstError error =  new OstError("wf_asddi_vadp_4", OstErrors.ErrorCode.INVALID_DEVICE_ADDRESS);
                error.addErrorInfo("qr_device_address", deviceAddress);
                error.addErrorInfo("userId", userId);
                error.addErrorInfo("reason", "Device with specified address does not exist for this user");
                throw error;
            }

            // Validate the Api Key Address
//            String deviceApiSignerAddress = ostDevice.getApiSignerAddress();
//            if ( null == deviceApiSignerAddress || !deviceApiSignerAddress.equalsIgnoreCase(apiKeyAddress) ) {
//                OstError error =  new OstError("wf_asddi_vadp_5", OstErrors.ErrorCode.INVALID_API_SIGNER_ADDRESS);
//                error.addErrorInfo("qr_device_address", deviceAddress);
//                error.addErrorInfo("qr_api_signer_address", apiKeyAddress);
//                error.addErrorInfo("actual_api_signer_address", deviceApiSignerAddress);
//                error.addErrorInfo("userId", userId);
//                error.addErrorInfo("reason", "Invalid api signer address");
//                throw error;
//            }

            // Ensure device is in registered state.
            if ( !ostDevice.canBeAuthorized() ) {
                OstError error =  new OstError("wf_asddi_vadp_6", OstErrors.ErrorCode.INVALID_DEVICE_ADDRESS);
                error.addErrorInfo("qr_device_address", deviceAddress);
                error.addErrorInfo("userId", userId);
                error.addErrorInfo("device_status", ostDevice.getStatus() );
                error.addErrorInfo("reason", "external session key to be authorized must be created from device with registered status");
                throw error;
            }

            // Ensure session does not exists.
            try {
                // apiClient.getSession should throw OstApiError.
                apiClient.getSession(externalSessionAddress);


                // If not, session seems to be already existing.
                OstSession session = OstSession.getById( externalSessionAddress );
                OstError error =  new OstError("wf_asddi_vadp_7", OstErrors.ErrorCode.INVALID_SESSION_ADDRESS);
                error.addErrorInfo("qr_session_address", externalSessionAddress);
                error.addErrorInfo("userId", userId);
                error.addErrorInfo("session_status", session.getStatus() );
                error.addErrorInfo("reason", "Session can not be authorized.");
                throw error;

            } catch (OstApiError apiError) {
                // Ignore. We expect an apiError.
            }
        }

        boolean isValidAddress(String address) {
            if ( TextUtils.isEmpty(address) || !WalletUtils.isValidAddress(address) ) {
                return false;
            }
            return true;
        }

        @Override
        public OstWorkflowContext.WORKFLOW_TYPE getWorkFlowType() {
            return OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_SESSION_WITH_QR_CODE;
        }

        @Nullable JSONObject getSessionData() {
            return dataObject.optJSONObject(OstConstants.QR_SESSION_DATA);
        }


        @Nullable String getExpiryTimestamp() {
            JSONObject sessionData = getSessionData();
            if ( null == sessionData ) {
                return null;
            }
            return sessionData.optString(OstConstants.QR_EXPIRY_TIMESTAMP);
        }

        @Nullable String getSpendingLimit() {
            JSONObject sessionData = getSessionData();
            if ( null == sessionData ) {
                return null;
            }
            return sessionData.optString(OstConstants.QR_SPENDING_LIMIT);
        }

        @Nullable String getSessionAddress() {
            JSONObject sessionData = getSessionData();
            if ( null == sessionData ) {
                return null;
            }

            String address = sessionData.optString(OstConstants.QR_SESSION_ADDRESS);
            if ( TextUtils.isEmpty(address) || !WalletUtils.isValidAddress(address) ) {
                return  address;
            }

            return Keys.toChecksumAddress(address);
        }


        @Nullable String getApiSignerAddress() {
            JSONObject sessionData = getSessionData();
            if ( null == sessionData ) {
                return null;
            }

            String address = sessionData.optString(OstConstants.QR_API_KEY_ADDRESS);
            if ( TextUtils.isEmpty(address) || !WalletUtils.isValidAddress(address) ) {
                return  address;
            }

            return Keys.toChecksumAddress(address);
        }

        @Override
        @Nullable String getDeviceAddress() {
            JSONObject sessionData = getSessionData();
            if ( null == sessionData ) {
                return null;
            }

            String address = sessionData.optString(OstConstants.QR_DEVICE_ADDRESS);
            if ( TextUtils.isEmpty(address) || !WalletUtils.isValidAddress(address) ) {
                return  address;
            }

            return Keys.toChecksumAddress(address);
        }

        String getSignature() {
            return dataObject.optString(OstConstants.QR_SIGNATURE);
        }
    }

    @Override
    AsyncStatus postFlowComplete(OstContextEntity ostContextEntity) {
        wipeSession(mSessionAddressToBeAdded);
        return super.postFlowComplete(ostContextEntity);
    }

    private void wipeSession(String address) {
        OstModelFactory.getSessionModel().deleteEntity(address);
        new OstSessionKeyModelRepository().deleteSessionKey(address);
    }
}
