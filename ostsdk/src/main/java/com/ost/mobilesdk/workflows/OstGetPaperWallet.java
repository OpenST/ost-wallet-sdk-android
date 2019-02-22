package com.ost.mobilesdk.workflows;

import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.biometric.OstBiometricAuthentication;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

/**
 * 1. Ask for pin or biometric
 * 2. show 12 words
 */
public class OstGetPaperWallet extends OstBaseWorkFlow implements OstPinAcceptInterface {

    private static final String TAG = "OstGetPaperWallet";
    private int mPinAskCount = 0;

    private enum STATES {
        INITIAL,
        CANCELLED,
        AUTHENTICATED,
        PIN_ENTERED,
    }

    private STATES mCurrentState = STATES.INITIAL;
    private Object mStateObject = null;

    private void setFlowState(STATES currentState, Object stateObject) {
        this.mCurrentState = currentState;
        this.mStateObject = stateObject;
    }

    public OstGetPaperWallet(String userId, OstWorkFlowCallback callback) {
        super(userId, callback);
    }

    synchronized public AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:
                Log.d(TAG, String.format("Perform workflow for userId: %s started", mUserId));

                Log.i(TAG, "Validating  params");
                if (!validateParams()) {
                    Log.e(TAG, "User Id is not valid");
                    return postErrorInterrupt("wf_gpw_pr_1", OstErrors.ErrorCode.INVALID_WORKFLOW_PARAMS);
                }

                Log.i(TAG, "Authenticating");
                if (shouldAskForBioMetric()) {
                    new OstBiometricAuthentication(OstSdk.getContext(), getBioMetricCallBack());
                } else {
                    postGetPin(OstGetPaperWallet.this);
                }
                break;
            case PIN_ENTERED:
                Log.i(TAG, "Pin Entered");
                String[] strings = ((String)mStateObject).split(" ");
                String uPin = strings[0];
                String appSalt = strings[0];
                if(validatePin(uPin, appSalt)) {
                    Log.d(TAG, "Pin Validated");
                    postPinValidated();
                } else {
                    mPinAskCount = mPinAskCount + 1;
                    if (mPinAskCount > OstConstants.MAX_PIN_LIMIT) {
                        Log.d(TAG, "Max pin ask limit reached");
                        return postErrorInterrupt("ef_pe_pr_2", OstErrors.ErrorCode.MAX_PIN_LIMIT_REACHED);
                    }
                    Log.d(TAG, "Pin InValidated ask for pin again");
                    return postInvalidPin(OstGetPaperWallet.this);
                }
            case AUTHENTICATED:
                Log.i(TAG, "Fetch 12 words");
                OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
                String[] mnemonicsArray = ostKeyManager.getMnemonics();

                postMnemonics(mnemonicsArray);
                postFlowComplete();
                break;
            case CANCELLED:
                Log.d(TAG, String.format("Error in Add device flow: %s", mUserId));
                postErrorInterrupt("wf_gpw_pr_5",OstErrors.ErrorCode.WORKFLOW_CANCELED);
                break;
        }
        return new AsyncStatus(true);
    }

    private AsyncStatus postMnemonics(String[] mnemonicsArray) {
        Log.i(TAG, "show mnemonics");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.showPaperWallet(mnemonicsArray);
            }
        });
        return new AsyncStatus(true);
    }

    private boolean validateParams() {
        return null != OstUser.getById(mUserId);
    }

    

    private String getDeviceManagerAddress() {
        return OstUser.getById(mUserId).getDeviceManagerAddress();
    }
    

    @Override
    public void pinEntered(String uPin, String appUserPassword) {
        setFlowState(OstGetPaperWallet.STATES.PIN_ENTERED, String.format("%s %s", uPin, appUserPassword));
        perform();
    }

    @Override
    public void cancelFlow(OstError ostError) {
        setFlowState(OstGetPaperWallet.STATES.CANCELLED, ostError);
        perform();
    }

    @Override
    void onBioMetricAuthenticationSuccess() {
        super.onBioMetricAuthenticationSuccess();
        setFlowState(STATES.AUTHENTICATED, null);
        perform();
    }

    @Override
    void onBioMetricAuthenticationFail() {
        super.onBioMetricAuthenticationFail();
        setFlowState(STATES.CANCELLED, null);
        perform();
    }
}