/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.sdkInteract;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;

public class SdkInteract {
    private static final SdkInteract INSTANCE = new SdkInteract();

    //It holds all the work flow listeners
    private List<WorkFlowListener> workFlowListenerList = new LinkedList<>();

    //It holds all the subscribed callbacks
    private WeakHashMap<String, List<WeakReference<SdkInteractListener>>> sdkListeners = new WeakHashMap<>();

    private SdkInteractListener mPinCallbackListener;
    private SdkInteractListener mVerifyDataCallbackListener;
    private SdkInteractListener mFlowListener;

    PinCallback getPinCallbackListener() {
        return (PinCallback) mPinCallbackListener;
    }

    public VerifyDataCallback getVerifyDataCallbackListener() {
        return (VerifyDataCallback) mVerifyDataCallbackListener;
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
        return workFlowListener;
    }

    /**
     * It unregister the provided workflow listener
     * @param workFlowListener WorkFlowListener object that implements OstWorkFlowCallback
     */
    void unRegister(WorkFlowListener workFlowListener) {
        workFlowListenerList.remove(workFlowListener);
    }

    /**
     * Use it to subscribe for any particular Workflow callback
     * @param workflowId Integer work flow listener Id
     * @param listener SdkInteractListener object that implements respective workflow callback
     */
    public void subscribe(String workflowId, SdkInteractListener listener) {
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

    void notifyEvent(String workflowId, CALLBACK_TYPE callback_type, Object... objects) {
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


    private void fireEventForCallbackType(String workflowId, SdkInteractListener sdkInteractListener, CALLBACK_TYPE callback_type, Object... objects) {
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
            public void verifyData(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {

            }
        };
        mPinCallbackListener = new PinCallback() {
            @Override
            public void getPin(String workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

            }

            @Override
            public void invalidPin(String workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

            }

            @Override
            public void pinValidated(String workflowId, OstWorkflowContext ostWorkflowContext, String userId) {

            }
        };
    }

    interface SdkInteractListener {

    }

    public interface FlowComplete extends SdkInteractListener {
        void flowComplete(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
    }

    public interface FlowInterrupt extends SdkInteractListener {
        void flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError);
    }

    public interface RequestAcknowledged extends SdkInteractListener {
        void requestAcknowledged(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);
    }

    public interface PinCallback extends SdkInteractListener {

        void getPin(String workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);

        void invalidPin(String workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);

        void pinValidated(String workflowId, OstWorkflowContext ostWorkflowContext, String userId);
    }

    public interface VerifyDataCallback extends SdkInteractListener {

        void verifyData(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface);
    }
}