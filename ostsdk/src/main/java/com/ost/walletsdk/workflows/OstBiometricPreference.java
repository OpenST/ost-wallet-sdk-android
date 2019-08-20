package com.ost.walletsdk.workflows;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.OstBiometricManager;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

public class OstBiometricPreference extends OstBaseWorkFlow {

    private final boolean mEnable;

    public OstBiometricPreference(String userId, boolean enable, OstWorkFlowCallback callback) {
        super(userId, callback);
        this.mEnable = enable;
    }

    @Override
    boolean shouldCheckCurrentDeviceAuthorization() {
        return true;
    }

    @Override
    protected boolean shouldAskForAuthentication() {
        return true;
    }

    @Override
    boolean shouldAskForBioMetric() {
        return false;
    }

    @Override
    AsyncStatus performOnAuthenticated() {
        OstBiometricManager ostBiometricManager = new OstBiometricManager(mUserId);
        if (mEnable) {
            ostBiometricManager.enableBiometric();
        } else {
            ostBiometricManager.disableBiometric();
        }
        return postFlowComplete( new OstContextEntity(mEnable, OstSdk.BOOLEAN));
    }

    @Override
    public OstWorkflowContext.WORKFLOW_TYPE getWorkflowType() {
        return OstWorkflowContext.WORKFLOW_TYPE.UPDATE_BIOMETRIC_PREFERENCE;
    }
}