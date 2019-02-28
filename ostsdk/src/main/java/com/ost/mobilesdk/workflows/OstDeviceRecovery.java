package com.ost.mobilesdk.workflows;

import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.web3j.crypto.Keys;

public class OstDeviceRecovery extends OstBaseWorkFlow {

    private final String mAppSalt;
    private final String mPin;
    private final String mAddressToReplace;
    private final TYPE mRecoveryType;

    private OstDeviceRecovery.STATES mCurrentState = OstDeviceRecovery.STATES.INITIAL;

    public OstDeviceRecovery(String userId, String appSalt, String pin, String addressToReplace, TYPE recoveryType, OstWorkFlowCallback callback) {
        super(userId, callback);
        mAppSalt = appSalt;
        mPin = pin;
        mAddressToReplace = Keys.toChecksumAddress(addressToReplace);
        mRecoveryType = recoveryType;
    }

    @Override
    protected AsyncStatus process() {
        switch (mCurrentState) {
            case INITIAL:

            case POLLING:
                break;
            case CANCELLED:
        }
        return new AsyncStatus(true);
    }

    private enum STATES {
        INITIAL,
        CANCELLED,
        POLLING
    }

    public enum TYPE {
        INITIATE_DEVICE_RECOVERY,
        REVOKE_DEVICE_RECOVERY
    }
}