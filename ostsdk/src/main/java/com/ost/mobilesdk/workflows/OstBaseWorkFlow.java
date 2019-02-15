package com.ost.mobilesdk.workflows;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.security.OstKeyManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import java.util.concurrent.Future;

abstract class OstBaseWorkFlow {
    private static final String TAG = "OstBaseWorkFlow";

    final String mUserId;
    final Handler mHandler;
    final OstWorkFlowCallback mCallback;

    OstBaseWorkFlow(String userId, Handler handler, OstWorkFlowCallback callback) {
        mUserId = userId;
        mHandler = handler;
        mCallback = callback;
    }

    boolean hasValidParams() {
        return !TextUtils.isEmpty(mUserId) && null != mHandler && null != mCallback;
    }
    public Future<AsyncStatus> perform() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                return process();
            }
        });
    }

    protected abstract AsyncStatus process();


    void postFlowComplete() {
        Log.i(TAG, "Flow complete");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowComplete(new OstContextEntity());
            }
        });
    }

    void postError(String msg) {
        Log.i(TAG, "Flow Error");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.flowInterrupt(new OstError(msg));
            }
        });
    }

    boolean hasCreatedDevice() {
        OstDevice ostDevice = OstUser.getById(mUserId).getCurrentDevice();
        return hasCreatedDevice(ostDevice);
    }

    boolean hasCreatedDevice(OstDevice ostDevice) {
        OstKeyManager ostKeyManager = new OstKeyManager(mUserId);
        return ostKeyManager.getApiKeyAddress().equalsIgnoreCase(ostDevice.getPersonalSignAddress())
                && (OstDevice.CONST_STATUS.CREATED.equals(ostDevice.getStatus()));
    }
}