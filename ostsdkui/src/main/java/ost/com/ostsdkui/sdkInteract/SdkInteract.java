/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui.sdkInteract;

import android.support.annotation.Nullable;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;

public class SdkInteract {
    private static final SdkInteract INSTANCE = new SdkInteract();

    //It holds all the work flow listeners
    private List<WorkFlowListener> workFlowListenerList = new LinkedList<>();
    private HashMap<Long, WorkFlowListener> listenerHashMap = new HashMap<>();

    //It holds all the subscribed callbacks
    private WeakHashMap<Long, List<WeakReference<SdkInteractListener>>> sdkListeners = new WeakHashMap<>();

    private SdkInteractListener mPinCallbackListener;
    private SdkInteractListener mVerifyDataCallbackListener;
    private SdkInteractListener mFlowListener;
    private SdkHelperCallback mSdkHelperCallback;

    PinCallback getPinCallbackListener() {
        return (PinCallback) mPinCallbackListener;
    }

    public VerifyDataCallback getVerifyDataCallbackListener() {
        return (VerifyDataCallback) mVerifyDataCallbackListener;
    }

    public void setSdkHelper(SdkHelperCallback sdkHelperCallback) {
        this.mSdkHelperCallback = sdkHelperCallback;
    }

    public void getUserPinSalt(UserPinSaltCallback userPinSaltCallback) {
        mSdkHelperCallback.getUserPinSalt(userPinSaltCallback);
    }

    public void setFlowListeners(SdkInteractListener sdkInteractListener) {
        mFlowListener = sdkInteractListener;
    }

    public SdkInteractListener getFlowListener() {
        return mFlowListener;
    }

    enum CALLBACK_TYPE {
        ALL,
        REGISTER_DEVICE,
        PIN_VALIDATED,
        INVALID_PIN,
        GET_PIN,
        FLOW_COMPLETE,
        FLOW_INTERRUPT,
        REQUEST_ACK,
        VERIFY_DATA,
    }

    public static SdkInteract getInstance() {
        return INSTANCE;
    }

    /**
     * Creates new Object of Work flow Listener
     * @return WorkFlowListener object that implements OstWorkFlowCallback
     */
    public synchronized WorkFlowListener newWorkFlowListener() {
        WorkFlowListener workFlowListener = new WorkFlowListener();
        workFlowListenerList.add(workFlowListener);
        listenerHashMap.put(workFlowListener.getId(), workFlowListener);
        return workFlowListener;
    }

    /**
     * Get Object of Work flow Listener
     * @return WorkFlowListener object that implements OstWorkFlowCallback
     */
    public synchronized WorkFlowListener getWorkFlowListener(long id) {
        return listenerHashMap.get(id);
    }

    /**
     * It unregister the provided workflow listener
     * @param workFlowListener WorkFlowListener object that implements OstWorkFlowCallback
     */
    void unRegister(WorkFlowListener workFlowListener) {
        workFlowListenerList.remove(workFlowListener);
        listenerHashMap.remove(workFlowListener.getId());
    }

    /**
     * Use it to subscribe for any particular Workflow callback
     * @param workflowId Integer work flow listener Id
     * @param listener SdkInteractListener object that implements respective workflow callback
     */
    public void subscribe(long workflowId, SdkInteractListener listener) {
        List<WeakReference<SdkInteractListener>> weakList = sdkListeners.get(workflowId);
        if (null == weakList) {
            weakList = new LinkedList<>();
            sdkListeners.put(workflowId, weakList);
        }
        ListIterator<WeakReference<SdkInteractListener>> iter = weakList.listIterator();
        while(iter.hasNext()){
            WeakReference<SdkInteractListener> weakSdkListener = iter.next();
            if(weakSdkListener.get().equals(listener)){
                return;
            }
        }
        weakList.add(new WeakReference<>(listener));
    }

    public void unSubscribe(long workflowId, SdkInteractListener listener) {
        List<WeakReference<SdkInteractListener>> weakList = sdkListeners.get(workflowId);
        if (null != weakList) {
            List<Integer> listToRemove = new ArrayList<>();
            for (int i = 0; i<weakList.size(); i++) {
                WeakReference<SdkInteractListener> weakSdkListener = weakList.get(i);
                if (weakSdkListener.get() == null || weakSdkListener.get().equals(listener)) {
                    listToRemove.add(i);
                }
            }
            for (int index: listToRemove) {
                weakList.remove(index);
            }
        }
    }

    public void setPinCallbackListener(PinCallback listener) {
        mPinCallbackListener = listener;
    }

    public void setVerifyDataCallbackListener(VerifyDataCallback listener) {
        mVerifyDataCallbackListener = listener;
    }

    void notifyEvent(long workflowId, CALLBACK_TYPE callback_type, Object... objects) {
        //Generic notification
        fireEventForCallbackType(workflowId, getFlowListener(), callback_type, objects);

        List<WeakReference<SdkInteractListener>> weakList = sdkListeners.get(workflowId);
        if (null != weakList) {
            List<Integer> listToRemove = new ArrayList<>();
            for (int i = 0; i<weakList.size(); i++) {
                WeakReference<SdkInteractListener> weakSdkListener = weakList.get(i);
                if (weakSdkListener.get() == null) {
                    listToRemove.add(i);
                } else {
                    fireEventForCallbackType(workflowId, weakSdkListener.get(), callback_type, objects);
                }
            }
            for (int index: listToRemove) {
                weakList.remove(index);
            }
        }
    }


    private void fireEventForCallbackType(long workflowId, SdkInteractListener sdkInteractListener, CALLBACK_TYPE callback_type, Object... objects) {
        switch (callback_type) {

            case FLOW_COMPLETE:
                if (sdkInteractListener instanceof FlowComplete) {
                    ((FlowComplete) sdkInteractListener).flowComplete(
                            workflowId,
                            (OstWorkflowContext) objects[0],
                            (OstContextEntity) objects[1]
                    );
                }

                break;
            case FLOW_INTERRUPT:
                if (sdkInteractListener instanceof FlowInterrupt) {
                    ((FlowInterrupt) sdkInteractListener).flowInterrupt(
                            workflowId,
                            (OstWorkflowContext) objects[0],
                            (OstError) objects[1]
                    );
                }
                break;

            case REQUEST_ACK:
                if (sdkInteractListener instanceof RequestAcknowledged) {
                    ((RequestAcknowledged) sdkInteractListener).requestAcknowledged(
                            workflowId,
                            (OstWorkflowContext) objects[0],
                            (OstContextEntity) objects[1]
                    );
                }
                break;
        }
    }

    private SdkInteract() {
        /*
         * To avoid null pointer exception
         */
        mVerifyDataCallbackListener = new VerifyDataCallback() {
            @Override
            public void verifyData(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {

            }
        };
        mPinCallbackListener = new PinCallback() {
            @Override
            public void getPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

            }

            @Override
            public void invalidPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

            }

            @Override
            public void pinValidated(long workflowId, OstWorkflowContext ostWorkflowContext, String userId) {

            }
        };
    }

    interface SdkInteractListener {

    }

    public interface FlowComplete extends SdkInteractListener {
        void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
    }

    public interface FlowInterrupt extends SdkInteractListener {
        void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError);
    }

    public interface RequestAcknowledged extends SdkInteractListener {
        void requestAcknowledged(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
    }

    public interface PinCallback extends SdkInteractListener {

        void getPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);

        void invalidPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);

        void pinValidated(long workflowId, OstWorkflowContext ostWorkflowContext, String userId);
    }

    public interface VerifyDataCallback extends SdkInteractListener {

        void verifyData(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface);
    }

    public interface SdkHelperCallback {
        void getUserPinSalt(UserPinSaltCallback userPinSaltCallback);
        void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface);
        void deviceUnauthorized(OstError ostError);
    }

    public interface UserPinSaltCallback {
        void onResponse(@Nullable String salt);
    }
}