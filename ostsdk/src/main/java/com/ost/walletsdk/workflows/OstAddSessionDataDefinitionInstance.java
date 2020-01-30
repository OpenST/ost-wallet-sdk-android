package com.ost.walletsdk.workflows;

import android.text.TextUtils;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstSession;
import com.ost.walletsdk.network.OstApiClient;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class OstAddSessionDataDefinitionInstance extends OstDeviceDataDefinitionInstance {
    private static final String TAG = "AddSessionDDInstance";

    public OstAddSessionDataDefinitionInstance(JSONObject dataObject, String qrVersion, String userId, OstWorkFlowCallback callback) {
        super(dataObject, userId, callback);
        this.qrVersion = qrVersion;
    }

    public static OstAddSessionDataDefinitionInstance fromV2QR(String v2QrString, String userId, OstWorkFlowCallback callback ) {
        JSONObject qrPayload = getPayloadFromV2QR(v2QrString);
        JSONObject dataObject = null;
        String version = "UNKNOWN";

        if ( null != qrPayload ) {
            //Invalid QR.
            dataObject = qrPayload.optJSONObject( OstConstants.QR_DATA);
            version = qrPayload.optString(OstConstants.QR_DATA_DEFINITION_VERSION, version);
        }

        if ( null == dataObject ) {
            dataObject = new JSONObject();
        }

        return new OstAddSessionDataDefinitionInstance(dataObject, version, userId, callback);
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

    //region - QR Data Validations
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

        // Validate Session Data is signed by external device's api-signer
        ensureSessionDataSignedByApiSigner();

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


    public void ensureSessionDataSignedByApiSigner() {

        JSONObject sessionData = getSessionData();
        OstError error = new OstError("wf_asddi_esdsbts_0", OstErrors.ErrorCode.INVALID_QR_CODE);
        if ( null == sessionData ) {
            error.addErrorInfo("qr_session_data", "null");
            error.setInternalErrorCode("wf_asddi_esdsbts_1");
            throw error;
        }
        error.addErrorInfo("qr_session_data", sessionData.toString());

        String signature = getSignature();
        if ( null == signature) {
            error.addErrorInfo("qr_signature", null);
            error.setInternalErrorCode("wf_asddi_esdsbts_2");
            throw error;
        }
        error.addErrorInfo("qr_signature", signature);

        String externalDeviceAddress = getDeviceAddress();
        if ( null == externalDeviceAddress) {
            error.addErrorInfo("qr_external_device", "null");
            error.setInternalErrorCode("wf_asddi_esdsbts_3");
            throw error;
        }
        error.addErrorInfo("qr_external_device", externalDeviceAddress);

        OstDevice externalDevice = OstDevice.getById( externalDeviceAddress );
        if ( null == externalDevice) {
            error.addErrorInfo("qr_external_device_obj", "null");
            error.setInternalErrorCode("wf_asddi_esdsbts_4");
            throw error;
        }

        String externalApiSigner = externalDevice.getApiSignerAddress();
        if ( null == externalApiSigner ) {
            error.addErrorInfo("qr_external_device_api_signer", "null");
            error.setInternalErrorCode("wf_asddi_esdsbts_5");
            throw error;
        }
        error.addErrorInfo("qr_external_device_api_signer", externalApiSigner);

        String message = getMessageToSign();
        if ( null == message) {
            error.addErrorInfo("session_data_as_message", "null");
            error.setInternalErrorCode("wf_asddi_esdsbts_6");
            throw error;
        }
        error.addErrorInfo("session_data_as_message", message);

        String messageSigner = getPersonalMessageSigner(message, signature);
        if ( null == messageSigner) {
            error.addErrorInfo("recovered_message_signer", "null");

            // Clone error and throw.
            JSONObject errorInfo = error.getErrorInfo();
            error = new OstError("wf_asddi_esdsbts_7", OstErrors.ErrorCode.INVALID_SIGNATURE);
            error.setErrorInfo( errorInfo );
            throw error;
        }
        error.addErrorInfo("recovered_message_signer", messageSigner);

        if ( !messageSigner.equalsIgnoreCase(externalApiSigner) ) {
            // Clone error and throw.
            JSONObject errorInfo = error.getErrorInfo();
            error = new OstError("wf_asddi_esdsbts_8", OstErrors.ErrorCode.INVALID_SIGNATURE);
            error.setErrorInfo(errorInfo);
            throw error;
        }
    }


    boolean isValidAddress(String address) {
        if ( TextUtils.isEmpty(address) || !WalletUtils.isValidAddress(address) ) {
            return false;
        }
        return true;
    }
    //endregion


    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkFlowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_SESSION_WITH_QR_CODE;
    }

    //region - qr data getters
    public static JSONObject getPayloadFromV2QR( @NonNull String v2Payload ) {
        if ( null == v2Payload ) {
            return null;
        }
        String[] parts = v2Payload.split("\\" + OstConstants.QR_V2_DELIMITER);

        // AS|version|device_address|session_address|spending_limit|expiry_timestamp|signature
        if ( parts.length < 7 ) {
            //Does not have sufficient parts.
            return null;
        }

        if ( !OstConstants.DATA_DEFINITION_AUTHORIZE_SESSION.equalsIgnoreCase( parts[0] ) ) {
            // Payload is not AUTHORIZE SESSION payload.
            return null;
        }

        try {
            JSONObject jsonPayload = new JSONObject();


            //Data-Definition
            jsonPayload.putOpt(OstConstants.QR_DATA_DEFINITION, appendHexPrefix(parts[0]));

            //Data-Definition-Version
            jsonPayload.putOpt(OstConstants.QR_DATA_DEFINITION_VERSION, appendHexPrefix(parts[1]));

            //Data
            JSONObject dataObject = new JSONObject();
            jsonPayload.putOpt(OstConstants.QR_DATA, dataObject);

                //Session-Data
                JSONObject sessionData = new JSONObject();
                dataObject.putOpt(OstConstants.QR_SESSION_DATA, sessionData);

                    //Device-Address
                    sessionData.putOpt(OstConstants.QR_DEVICE_ADDRESS, appendHexPrefix(parts[2]));

                    //Session-Address
                    sessionData.putOpt(OstConstants.QR_SESSION_ADDRESS, appendHexPrefix(parts[3]));

                    //Session-Spending-Limit
                    sessionData.putOpt(OstConstants.QR_SPENDING_LIMIT, parts[4]);

                    //Expiry-Timestamp
                    sessionData.putOpt(OstConstants.QR_EXPIRY_TIMESTAMP, parts[5]);

                //Signature
                dataObject.putOpt(OstConstants.QR_SIGNATURE, appendHexPrefix(parts[6]));

                //The original payload
                dataObject.putOpt(OstConstants.QR_V2_INPUT, v2Payload);

            return jsonPayload;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    String getDataDefination() {
        return OstConstants.DATA_DEFINITION_AUTHORIZE_SESSION;
    }

    private final String qrVersion;
    @Nullable String getQRVersion() {
        return qrVersion;
    }

    @Nullable String getMessageToSign() {
        try {
            ArrayList<String> parts = new ArrayList<>();
            String hexString;

            //AS
            parts.add( getDataDefination() );

            //Version
            parts.add( getQRVersion() );

            //Device-Address
            hexString = getDeviceAddress();
            parts.add( stripHexPrefix(hexString) );

            //Session-Address
            hexString = getSessionAddress();
            parts.add( stripHexPrefix(hexString) );

            //Session-Spending-Limit
            parts.add( getSpendingLimit() );

            //Expiry-Timestamp
            parts.add( getExpiryTimestamp() );

            // Join with delimiter
            String messageToSign = TextUtils.join(OstConstants.QR_V2_DELIMITER, parts);

            // Lowercase it.
            return messageToSign.toLowerCase();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }

    @Nullable JSONObject getSessionData() {
        return dataObject.optJSONObject(OstConstants.QR_SESSION_DATA);
    }

    String getSignature() {
        return dataObject.optString(OstConstants.QR_SIGNATURE);
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
    //endregion

    //region - personal sign verifier.
    public static String getPersonalMessageSigner(String message, String signature) {
        String signerAddress = null;
        try {
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            byte v = signatureBytes[64];
            if (v < 27) {
                v += 27;
            }

            Sign.SignatureData signatureData =
                    new Sign.SignatureData(
                            v,
                            (byte[]) Arrays.copyOfRange(signatureBytes, 0, 32),
                            (byte[]) Arrays.copyOfRange(signatureBytes, 32, 64));

            BigInteger signerPublicKey = Sign.signedPrefixedMessageToKey(message.getBytes(), signatureData);
            String keyAddress = Keys.getAddress(signerPublicKey);
            signerAddress = Keys.toChecksumAddress( keyAddress );
        } catch (Throwable th) {
            th.printStackTrace();
        }

        return signerAddress;
    }

    @NonNull static String appendHexPrefix(@NonNull String str) {
        if ( str.startsWith("0x") ) {
            return str;
        }

        return "0x" + str;
    }

    @NonNull static String stripHexPrefix(@NonNull String hexString) {

        if ( !hexString.startsWith("0x") ) {
            return hexString;
        }

        if ( hexString.length() < 3 ) {
            // return empty string.
            return "";
        }

        return hexString.substring(2);
    }
    //endregion

}
