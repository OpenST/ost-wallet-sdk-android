package com.ost.mobilesdk.workflows;

import android.util.Log;

import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

/**
 * 1. Ask for pin or biometric
 * 2. show 12 words
 */
public class OstGetPaperWallet extends OstBaseUserAuthenticatorWorkflow {

    private static final String TAG = "OstGetPaperWallet";

    public OstGetPaperWallet(String userId, OstWorkFlowCallback callback) {
        super(userId, callback);
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        OstKeyManager ostKeyManager = null;
        try {
            ostKeyManager = new OstKeyManager(mUserId);
            postMnemonics(ostKeyManager.getMnemonics());
            return super.performOnAuthenticated();
        } catch (OstError error) {
            return postErrorInterrupt( error );
        } catch (Throwable th) {
            return postErrorInterrupt("wf_ogpw_poa_1", OstErrors.ErrorCode.UNKNOWN);
        } finally {
            ostKeyManager = null;
        }
    }

    private void postMnemonics(byte[] mnemonics) {
        Log.i(TAG, "show mnemonics");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.showPaperWallet(mnemonics);
            }
        });
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.GET_PAPER_WALLET;
    }
}