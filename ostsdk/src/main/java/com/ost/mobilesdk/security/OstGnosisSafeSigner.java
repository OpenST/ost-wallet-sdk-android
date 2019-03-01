package com.ost.mobilesdk.security;

import android.util.Log;

import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.structs.BaseDeviceManagerOperationStruct;
import com.ost.mobilesdk.security.structs.SignedAddSessionStruct;
import com.ost.mobilesdk.utils.EIP712;
import com.ost.mobilesdk.utils.GnosisSafe;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONObject;
import org.web3j.crypto.Keys;

public class OstGnosisSafeSigner {
    private static final String TAG = "OstGnosisSafeSigner";
    private final String mUserId;
    public OstGnosisSafeSigner(String userId) {
        mUserId = userId;
    }

    public SignedAddSessionStruct addSession(String sessionAddress, String spendingLimit, String expiryHeight) throws OstError {
        OstUser user = OstUser.getById(mUserId);
        sessionAddress = Keys.toChecksumAddress(sessionAddress);
        SignedAddSessionStruct struct = new SignedAddSessionStruct(sessionAddress, spendingLimit, expiryHeight);

        //Set Executable Data
        String executableData = new GnosisSafe().getAuthorizeSessionExecutableData(sessionAddress, spendingLimit, expiryHeight);
        struct.setExecutableData(executableData);

        //Special Case: Change the To Address. TokenHolder is the to address in this case.
        struct.setTokenHolderAddress( user.getTokenHolderAddress() );


        //Set Common Data.
        setCommonData(struct);

        //Compute Message Hash
        try {
            String messageHash = new EIP712(struct.getTypedData()).toEIP712TransactionHash();
            struct.setMessageHash(messageHash);
        } catch (Exception e) {
            OstError ostError = new OstError("km_gss_as_5", ErrorCode.FAILED_TO_GENERATE_MESSAGE_HASH);
            throw ostError;
        }

        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        if ( !ikm.canSignWithSession(sessionAddress) ) {
            OstError ostError = new OstError("km_gss_as_6", ErrorCode.INVALID_SESSION_ADDRESS);
            throw ostError;
        }

        //Sign the data.
        signData(struct, ikm);

        //All good
        return struct;
    }

    private void setCommonData(BaseDeviceManagerOperationStruct struct) {

        OstUser user = OstUser.getById(mUserId);
        if (null == user) {
            OstError ostError = new OstError("km_gss_as_2", ErrorCode.INSUFFICIENT_DATA);
            throw ostError;
        }

        OstDeviceManager deviceManager = OstDeviceManager.getById(user.getDeviceManagerAddress());
        if ( null == deviceManager ) {
            OstError ostError = new OstError("km_gss_as_3", ErrorCode.INSUFFICIENT_DATA);
            throw ostError;
        }


        //Set device manager address
        struct.setDeviceManagerAddress(user.getDeviceManagerAddress());

        //Set token holder address
        if ( null == struct.getToAddress() ) {
            struct.setToAddress(user.getDeviceManagerAddress());
        }


        //Set nonce
        int nonce = deviceManager.getNonce();
        struct.setNonce( String.valueOf(nonce) );


        JSONObject typedData = new GnosisSafe.SafeTxnBuilder()
                .setAddOwnerExecutableData(struct.getExecutableData())
                .setToAddress( struct.getToAddress() )
                .setVerifyingContract(struct.getVerifyingContract())
                .setNonce( struct.getNonce() )
                .build();
        struct.setTypedData( typedData );
    }



    private void signData(BaseDeviceManagerOperationStruct struct, InternalKeyManager ikm) {
        String messageHash = struct.getMessageHash();
        //Check if we have a device key.
        KeyMetaStruct keyMeta = InternalKeyManager.getKeyMataStruct(mUserId);
        if ( null == keyMeta) {
            OstError error = new OstError("km_gss_sd_1", ErrorCode.DEVICE_UNAUTHORIZED);
            throw error;
        }

        if ( null == ikm ) {
            ikm = new InternalKeyManager(mUserId);
        }

        try {
            String signature = ikm.signWithDeviceKey(messageHash);
            struct.setSignature(signature);
            struct.setSignerAddress( keyMeta.deviceAddress );
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage() );
            ex.printStackTrace();

            OstError ostError = new OstError("km_gss_sd_2", ErrorCode.FAILED_TO_SIGN_DATA);
            ostError.setStackTrace( ex.getStackTrace() );
            throw ostError;
        }
    }



}
