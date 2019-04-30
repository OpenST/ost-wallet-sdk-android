/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.sdkInteract;

import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;

public class SdkInteract {
    private static final SdkInteract INSTANCE = new SdkInteract();

    //It holds all the work flow listeners
    private List<WorkFlowListener> workFlowListenerList = new LinkedList<>();

    //It holds all the subscribed callbacks
    private WeakHashMap<Long, List<WeakReference<SdkInteractListener>>> sdkListeners = new WeakHashMap<>();

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
            ListIterator<WeakReference<SdkInteractListener>> iter = weakList.listIterator();
            while(iter.hasNext()){
                WeakReference<SdkInteractListener> weakSdkListener = iter.next();
                if(weakSdkListener.get().equals(listener)){
                    iter.remove();
                }
            }
        }
    }

    void notifyEvent(long workflowId, CALLBACK_TYPE callback_type, Object... objects) {
        List<WeakReference<SdkInteractListener>> weakList = sdkListeners.get(workflowId);
        if (null != weakList) {
            ListIterator<WeakReference<SdkInteractListener>> iter = weakList.listIterator();
            while(iter.hasNext()){
                WeakReference<SdkInteractListener> weakSdkListener = iter.next();
                if(weakSdkListener.get() == null){
                    iter.remove();
                } else {
                    fireEventForCallbackType(workflowId, weakSdkListener.get(), callback_type, objects);
                }
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
}