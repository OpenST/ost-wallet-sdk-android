/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.ui.walletsetup.PinFragment;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkFlowPinFragment extends PinFragment {

    private OstPinAcceptInterface mOstPinAcceptInterface;
    private OnFragmentInteractionListener mListener;

    private String mWorkflowId;
    private String mUserId;
    private OstWorkflowContext mOstWorkflowContext;


    public static WorkFlowPinFragment newInstance(String heading) {
        return newInstance(heading, null, false);
    }

    public static WorkFlowPinFragment newInstance(String heading, String subHeading, boolean showBackButton) {
        WorkFlowPinFragment fragment = new WorkFlowPinFragment();
        Bundle args = new Bundle();
        args.putString(HEADING, heading);
        args.putString(SUB_HEADING, subHeading);
        args.putBoolean(SHOW_BACK_BUTTON, showBackButton);
        fragment.setArguments(args);
        return fragment;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public void setWorkflowContext(OstWorkflowContext ostWorkflowContext) {
        mOstWorkflowContext = ostWorkflowContext;
    }

    public void setWorkflowId(String workflowId) {
        mWorkflowId = workflowId;
    }

    public void setPinCallback(OstPinAcceptInterface ostPinAcceptInterface) {
        mOstPinAcceptInterface = ostPinAcceptInterface;
    }

    @Override
    protected void setListener(Context context) {
        super.setListener(context);
        if (context instanceof WorkFlowPinFragment.OnFragmentInteractionListener) {
            mListener = (WorkFlowPinFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected boolean onValidPin(String pin) {
        WorkFlowListener workFlowListener = SdkInteract.getInstance().getWorkFlowListener(mWorkflowId);
        workFlowListener.getPassphrase(mUserId, mOstWorkflowContext, new OstPassphraseAcceptor() {
            @Override
            public void setPassphrase(String passphrase) {
                UserPassphrase userPassphrase = new UserPassphrase(mUserId, pin, passphrase );
                mOstPinAcceptInterface.pinEntered(userPassphrase);

                //Close fragment by notifying DashBoard activity
                mListener.popTopFragment();
            }

            @Override
            public void cancelFlow() {
                Log.d("getPinSalt", "Error in fetching Pin Salt");
                pinSaltNotFetched();
            }
        });
        return true;
    }

    @Override
    public void goBack() {
        if (null != mOstPinAcceptInterface) {
            mOstPinAcceptInterface.cancelFlow();
        }
        super.goBack();
    }

    public WorkFlowPinFragment() {
        // Required empty public constructor
    }

    private void pinSaltNotFetched(){
        if(null != mListener){
            mListener.invalidPin(0, null, "", mOstPinAcceptInterface);
        }
        goBack();
    }

    public interface OnFragmentInteractionListener extends PinFragment.OnFragmentInteractionListener {
        void popTopFragment();
        void invalidPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);
    }
}