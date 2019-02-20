package com.ost.mobilesdk.workflows;

import android.os.Looper;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Helper Class to sync OstSdk entities
 */
class OstSdkSync {
    public enum SYNC_ENTITY {
        TOKEN,
        USER,
        DEVICE,
        SESSION,
        TOKEN_HOLDER,
        DEVICE_MANAGER
    }

    private static final String TAG = "OstSdkSync";

    private static final long WAIT_TIME = 20;

    private final String mUserId;
    private final SYNC_ENTITY[] mSyncParams;
    private CountDownLatch mCountDownLatch;

    public OstSdkSync(String userId) {
        mUserId = userId;
        mSyncParams = (SYNC_ENTITY[]) Arrays
                .asList(SYNC_ENTITY.TOKEN, SYNC_ENTITY.USER, SYNC_ENTITY.DEVICE, SYNC_ENTITY.SESSION,
                        SYNC_ENTITY.DEVICE_MANAGER, SYNC_ENTITY.TOKEN_HOLDER)
                .toArray();
    }

    public OstSdkSync(String userId, SYNC_ENTITY... sync_entities) {
        mUserId = userId;
        mSyncParams = sync_entities;
    }

    public void perform() {
        //Main Thread check
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException(String.format("%s: SyncOperation on MainThread Exception", TAG));
        }

        mCountDownLatch = new CountDownLatch(mSyncParams.length);

        for (SYNC_ENTITY entity : mSyncParams) {
            sync(entity);
        }

        try {
            mCountDownLatch.await(WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, String.format("Sync Wait TimeOutException: %s", e.getMessage()));
        }
    }

    private void sync(SYNC_ENTITY entity) {
        DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                try {
                    JSONObject response = null;
                    OstApiClient ostApiClient = new OstApiClient(mUserId);
                    OstUser ostUser = OstUser.getById(mUserId);
                    Log.i(TAG, String.format("Sync request for %s", entity.toString()));
                    if (SYNC_ENTITY.TOKEN == entity) {
                        response = ostApiClient.getToken();
                    } else if (SYNC_ENTITY.USER == entity) {
                        response = ostApiClient.getUser();
                    } else if (SYNC_ENTITY.DEVICE == entity) {
                        response = ostApiClient.getDevices(ostUser.getCurrentDevice().getAddress());
                    } else if (SYNC_ENTITY.SESSION == entity) {
                        List<OstSession> ostSessionList = OstSession.getSessionsToSync(mUserId);
                        for (OstSession ostSession: ostSessionList) {
                            response = ostApiClient.getSession(ostSession.getAddress());
                            OstSdk.parse(response);
                        }
                    } else if (SYNC_ENTITY.DEVICE_MANAGER == entity) {
                        response = ostApiClient.getDeviceManager();
                    } else if (SYNC_ENTITY.TOKEN_HOLDER == entity) {
                        response = ostApiClient.getTokenHolder();
                    }
                    Log.i(TAG, String.format("Sync response for %s", entity.toString()));
                    OstSdk.parse(response);
                } catch (IOException e) {
                    Log.e(TAG, String.format("IOException: %s", e.getCause()));
                } catch (JSONException e) {
                    Log.e(TAG, String.format("JSONException: %s", e.getCause()));
                } finally {
                    mCountDownLatch.countDown();
                }
                return new AsyncStatus(true);
            }
        });
    }
}