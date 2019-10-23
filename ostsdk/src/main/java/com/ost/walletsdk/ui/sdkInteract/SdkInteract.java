/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.sdkInteract;

import com.ost.walletsdk.annotations.Nullable;

import com.ost.walletsdk.ui.interfaces.FlowCompleteListener;
import com.ost.walletsdk.ui.interfaces.FlowInterruptListener;
import com.ost.walletsdk.ui.interfaces.OstWalletUIListener;
import com.ost.walletsdk.ui.interfaces.RequestAcknowledgedListener;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

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
    private HashMap<String, WorkFlowListener> listenerHashMap = new HashMap<>();

    //It holds all the subscribed callbacks
    private WeakHashMap<String, List<WeakReference<OstWalletUIListener>>> sdkListeners = new WeakHashMap<>();

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
     * Creates new Object of Work flow Listener
     * @param workFlowCallbacks workflow callbacks object for pin and verfiy data callbacks
     * @return WorkFlowListener object that implements OstWorkFlowCallback
     */
    public synchronized WorkFlowListener newWorkFlowListener(WorkFlowCallbacks workFlowCallbacks) {
        WorkFlowListener workFlowListener = new WorkFlowListener(workFlowCallbacks);
        workFlowListenerList.add(workFlowListener);
        listenerHashMap.put(workFlowListener.getId(), workFlowListener);
        return workFlowListener;
    }

    /**
     * Get Object of Work flow Listener
     * @return WorkFlowListener object that implements OstWorkFlowCallback
     */
    public synchronized WorkFlowListener getWorkFlowListener(String id) {
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
    public void subscribe(String workflowId, OstWalletUIListener listener) {
        List<WeakReference<OstWalletUIListener>> weakList = sdkListeners.get(workflowId);
        if (null == weakList) {
            weakList = new LinkedList<>();
            sdkListeners.put(workflowId, weakList);
        }
        ListIterator<WeakReference<OstWalletUIListener>> iter = weakList.listIterator();
        while(iter.hasNext()){
            WeakReference<OstWalletUIListener> weakSdkListener = iter.next();
            if(weakSdkListener.get().equals(listener)){
                return;
            }
        }
        weakList.add(new WeakReference<>(listener));
    }

    public void unsubscribe(String workflowId, OstWalletUIListener listener) {
        List<WeakReference<OstWalletUIListener>> weakList = sdkListeners.get(workflowId);
        if (null != weakList) {
            List<WeakReference<OstWalletUIListener>> listToRemove = new ArrayList<>();
            for (int i = 0; i<weakList.size(); i++) {
                WeakReference<OstWalletUIListener> weakSdkListener = weakList.get(i);
                if (weakSdkListener.get() == null || weakSdkListener.get().equals(listener)) {
                    listToRemove.add(weakSdkListener);
                }
            }
            weakList.removeAll(listToRemove);
        }
    }

    void notifyEvent(String workflowId, CALLBACK_TYPE callback_type, Object... objects) {
        //Generic notification
//        fireEventForCallbackType(workflowId, getFlowListener(), callback_type, objects);

        List<WeakReference<OstWalletUIListener>> weakList = sdkListeners.get(workflowId);
        if (null != weakList) {
            List<WeakReference<OstWalletUIListener>> listToRemove = new ArrayList<>();
            for (int i = 0; i<weakList.size(); i++) {
                WeakReference<OstWalletUIListener> weakSdkListener = weakList.get(i);
                if (weakSdkListener.get() == null) {
                    listToRemove.add(weakSdkListener);
                } else {
                    fireEventForCallbackType(workflowId, weakSdkListener.get(), callback_type, objects);
                }
            }
            weakList.removeAll(listToRemove);
        }
    }


    private void fireEventForCallbackType(String workflowId, OstWalletUIListener sdkInteractListener, CALLBACK_TYPE callback_type, Object... objects) {
        switch (callback_type) {

            case FLOW_COMPLETE:
                if (sdkInteractListener instanceof FlowCompleteListener) {
                    ((FlowCompleteListener) sdkInteractListener).flowComplete(
                            (OstWorkflowContext) objects[0],
                            (OstContextEntity) objects[1]
                    );
                }

                break;
            case FLOW_INTERRUPT:
                if (sdkInteractListener instanceof FlowInterruptListener) {
                    ((FlowInterruptListener) sdkInteractListener).flowInterrupt(
                            (OstWorkflowContext) objects[0],
                            (OstError) objects[1]
                    );
                }
                break;

            case REQUEST_ACK:
                if (sdkInteractListener instanceof RequestAcknowledgedListener) {
                    ((RequestAcknowledgedListener) sdkInteractListener).requestAcknowledged(
                            (OstWorkflowContext) objects[0],
                            (OstContextEntity) objects[1]
                    );
                }
                break;
        }
    }

    private SdkInteract() {

    }

    public interface WorkFlowCallbacks extends OstWalletUIListener {

        boolean getPin(String workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);

        boolean invalidPin(String workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);

        boolean pinValidated(String workflowId, OstWorkflowContext ostWorkflowContext, String userId);

        boolean flowComplete(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);

        boolean flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError);

        boolean requestAcknowledged(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity);

        boolean verifyData(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface);
    }

    public interface UserPinSaltCallback {
        void onResponse(@Nullable String salt);
    }
}