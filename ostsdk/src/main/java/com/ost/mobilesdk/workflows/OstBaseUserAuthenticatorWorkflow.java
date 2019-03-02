package com.ost.mobilesdk.workflows;

import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.biometric.OstBiometricAuthentication;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import java.util.ArrayList;


abstract public class OstBaseUserAuthenticatorWorkflow extends OstBaseWorkFlow implements OstPinAcceptInterface {
    private static String TAG = "BUAWorkFlow";
    private int mPinAskCount = 0;
    private String uPin;
    private String appUserPassword;
    protected WorkflowStateManager stateManager;

    OstBaseUserAuthenticatorWorkflow(String userId, OstWorkFlowCallback callback) {
        super(userId, callback);
        setStateManager();
    }

    protected void setStateManager() {
        stateManager = new WorkflowStateManager();
    }

    synchronized protected AsyncStatus process() {
        AsyncStatus status = null;
        String currentState = stateManager.getCurrentState();
        Object currentStateObject = stateManager.getStateObject();
        status = onStateChanged(currentState, currentStateObject);
        if ( null != status ) {
            return status;
        }
        return new AsyncStatus(true);
    }



    protected AsyncStatus performNext(Object stateObject) {
        stateManager.setNextState(stateObject);
        return process();
    }

    protected AsyncStatus goToState(String state, Object stateObject) {
        stateManager.setState(state, stateObject);
        return process();
    }

    protected void performWithState(String state, Object stateObject) {
        stateManager.setState(state, stateObject);
        perform();
    }

    //Helpers.
    protected AsyncStatus goToState(String state) {
        return goToState(state, null);
    }
    protected AsyncStatus performNext() {
        return performNext(null);
    }
    protected void performWithState(String state) {
        performWithState(state, null);
    }



    protected AsyncStatus onStateChanged(String state, Object stateObject) {
        try {
            switch (state) {
                case WorkflowStateManager.INITIAL:
                    return performValidations(stateObject);

                case WorkflowStateManager.PARAMS_VALIDATED:
                    return performUserDeviceValidation(stateObject);

                case WorkflowStateManager.DEVICE_VALIDATED:
                    Log.i(TAG, "Ask for authentication");
                    if (shouldAskForBioMetric()) {
                        new OstBiometricAuthentication(OstSdk.getContext(), getBioMetricCallBack());
                    } else {
                        return goToState(WorkflowStateManager.AUTHENTICATED);
                    }
                    break;

                case WorkflowStateManager.PIN_AUTHENTICATION_REQUIRED:
                    postGetPin(this);
                    break;

                case WorkflowStateManager.PIN_INFO_RECEIVED:
                    return verifyUserPin( (OstUserPinInfoHolder) stateObject );

                case WorkflowStateManager.AUTHENTICATED:
                    //Call the abstract method.
                    AsyncStatus status = performOnAuthenticated();
                    if ( !status.isSuccess() ) {
                        goToState(WorkflowStateManager.COMPLETED_WITH_ERROR);
                    }
                    return status;
                case WorkflowStateManager.CANCELLED:
                    if ( stateObject instanceof OstError) {
                        return postErrorInterrupt( (OstError) stateObject );
                    } else {
                        OstError error = new OstError("bua_wf_osc_canceled", ErrorCode.UNKNOWN);
                        return postErrorInterrupt(error);
                    }

                case WorkflowStateManager.COMPLETED:
                    return new AsyncStatus(true);

                case WorkflowStateManager.COMPLETED_WITH_ERROR:
                    return new AsyncStatus(false);
            }
        } catch (OstError ostError) {
            return postErrorInterrupt(ostError);
        } catch (Exception exception) {
            OstError ostError = new OstError("bua_wf_osc_1", ErrorCode.UNCAUGHT_EXCEPTION_HANDELED);
            ostError.setStackTrace(exception.getStackTrace());
            return postErrorInterrupt(ostError);
        }
        return new AsyncStatus(true);
    }



    protected AsyncStatus performValidations(Object stateObject) {
        Log.i(TAG, "Validating user Id");
        //To-Do: hasValidParams should throw errors. Rename it to validateParams.
        if (!hasValidParams()) {
            Log.e(TAG, String.format("Invalid params for userId : %s", mUserId));
            throw new OstError("bua_wf_pv_1", ErrorCode.INVALID_WORKFLOW_PARAMS);
        }
        return performNext();
    }

    protected AsyncStatus performUserDeviceValidation(Object stateObject) {

        try {
            //Ensure sdk can make Api calls
            ensureApiCommunication();

            // Ensure we have OstUser complete entity.
            ensureOstUser();

            // Ensure we have OstToken complete entity.
            ensureOstToken();

            if ( shouldCheckCurrentDeviceAuthorization() ) {
                //Ensure Device is Authorized.
                ensureDeviceAuthorized();

                //Ensures Device Manager is present as derived classes are likely going to need nonce.
                ensureDeviceManager();
            }

        } catch (OstError err) {
            return postErrorInterrupt(err);
        }

        return performNext();
    }




    @Override
    public void pinEntered(String uPin, String appUserPassword) {
        OstUserPinInfoHolder pinInfoHolder = new OstUserPinInfoHolder();
        pinInfoHolder.setPassphrasePrefix(appUserPassword);
        pinInfoHolder.setUserPassphrase(uPin);;
        performWithState(WorkflowStateManager.PIN_INFO_RECEIVED, pinInfoHolder);
        pinInfoHolder = null;

    }

    AsyncStatus verifyUserPin(OstUserPinInfoHolder infoHolder) {

        Log.i(TAG, "Pin Entered");
        String kitSalt = getSalt();
        if ( null == kitSalt ) {
            return postErrorInterrupt("bpawf_vup_1", ErrorCode.SALT_API_FAILED);
        }
        infoHolder.setScriptSalt(kitSalt);
        kitSalt = null;

        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        boolean isValid = ostKeyManager.validatePin(infoHolder);

        if ( isValid ) {
            postPinValidated();
            return goToState(WorkflowStateManager.AUTHENTICATED);
        }

        mPinAskCount = mPinAskCount + 1;
        if (mPinAskCount > OstConstants.MAX_PIN_LIMIT) {
            Log.d(TAG, "Max pin ask limit reached");
            return postErrorInterrupt("bpawf_vup_2", ErrorCode.MAX_PASSPHRASE_VERIFICATION_LIMIT_REACHED);
        }
        Log.d(TAG, "Pin InValidated ask for pin again");
        OstPinAcceptInterface me = (OstPinAcceptInterface) this;
        return postInvalidPin(me);
    }

    AsyncStatus postPinValidated() {
        Log.i(TAG, "Pin validated");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.pinValidated(mUserId);
            }
        });
        return new AsyncStatus(true);
    }

    AsyncStatus postInvalidPin(OstPinAcceptInterface pinAcceptInterface) {
        Log.i(TAG, "Invalid Pin");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.invalidPin(mUserId, pinAcceptInterface);
            }
        });
        return new AsyncStatus(true);
    }

    @Override
    public void cancelFlow(OstError ostError) {
        performWithState(WorkflowStateManager.CANCELLED);
    }

    @Override
    void onBioMetricAuthenticationSuccess() {
        super.onBioMetricAuthenticationSuccess();
        performWithState(WorkflowStateManager.AUTHENTICATED);
    }

    @Override
    void onBioMetricAuthenticationFail() {
        super.onBioMetricAuthenticationFail();
        //Ask for pin.
        performWithState(WorkflowStateManager.PIN_AUTHENTICATION_REQUIRED);
    }

    public static class WorkflowStateManager {
        ArrayList<String> orderedStates = new ArrayList<>();
        public static final String INITIAL = "INITIAL";
        public static final String PARAMS_VALIDATED = "PARAMS_VALIDATED";
        public static final String DEVICE_VALIDATED = "DEVICE_VALIDATED";
        public static final String PIN_AUTHENTICATION_REQUIRED = "PIN_AUTHENTICATION_REQUIRED";
        public static final String PIN_INFO_RECEIVED = "PIN_INFO_RECEIVED";
        public static final String AUTHENTICATED = "AUTHENTICATED";
        public static final String CANCELLED = "CANCELLED";
        public static final String COMPLETED_WITH_ERROR = "COMPLETED_WITH_ERROR";
        public static final String COMPLETED = "COMPLETED";


        private int mCurrentState = 0;


        private Object mStateObject = null;

        public WorkflowStateManager() {
            setSateOrder();
        }

        public void setSateOrder() {
            orderedStates.add(INITIAL);
            orderedStates.add(PARAMS_VALIDATED);
            orderedStates.add(DEVICE_VALIDATED);
            orderedStates.add(PIN_AUTHENTICATION_REQUIRED);
            orderedStates.add(PIN_INFO_RECEIVED);
            orderedStates.add(AUTHENTICATED);
            orderedStates.add(CANCELLED);
            orderedStates.add(COMPLETED);
            orderedStates.add(COMPLETED_WITH_ERROR);
        }


        public String getCurrentState() {
            return orderedStates.get(mCurrentState);
        }

        public Object getStateObject() {
            return mStateObject;
        }

        public String getNextState() {
            return orderedStates.get(mCurrentState + 1);
        }

        public void setCurrentStateObject(Object stateObject) {
            this.mStateObject = stateObject;
        }

        public void setNextState(Object stateObject) {
            mCurrentState += 1;
            this.mStateObject = stateObject;
        }
        public void setNextState() {
            setNextState(null);
        }

        public void setState(String state) {
            setState(state, null);
        }
        public void setState(String state, Object stateObject) {
            int stateIndx = orderedStates.indexOf(state);
            if ( stateIndx < 0 ) {
                OstError ostError = new OstError("bua_wf_WFSM_jts_1", ErrorCode.UNKNOWN);
                throw ostError;
            }
            mCurrentState = stateIndx;
            mStateObject = stateObject;
        }
    }

    abstract AsyncStatus performOnAuthenticated();

    boolean shouldCheckCurrentDeviceAuthorization() {
        return true;
    }
}
