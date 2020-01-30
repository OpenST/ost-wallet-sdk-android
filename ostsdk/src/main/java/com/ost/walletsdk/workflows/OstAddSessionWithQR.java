package com.ost.walletsdk.workflows;

import android.text.TextUtils;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.annotations.NonNull;
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
