package com.ost.mobilesdk.workflows;

import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DelayedRecoveryModule;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;
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

                String currentDeviceAddress = null;
                String recoveredDeviceAddress = null;
                String recoveredDeviceParentAddress = OstDevice.getById(recoveredDeviceAddress).getLinkedAddress();
                String recoveryAddress = OstUser.getById(mUserId).getRecoveryAddress();

//                String signature = getExecutableCallDataSignature(recoveredDeviceParentAddress, recoveredDeviceAddress,
//                        currentDeviceAddress, recoveryAddress);


            case POLLING:
                break;
            case CANCELLED:
        }
        return new AsyncStatus(true);
    }

    private String getExecutableCallDataSignature(String recoveredDeviceParentAddress, String recoveredDeviceAddress,
                                                  String currentDeviceAddress, String recoveryAddress) {
        JSONObject recoveryTypeData = new DelayedRecoveryModule().generateInitiateRecoveryOwnerData(recoveredDeviceParentAddress, recoveredDeviceAddress,
                currentDeviceAddress, recoveryAddress);


        //TODO :: Create Sign executable data of recoveryTypeDate using RecoverySigner
        return null;
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