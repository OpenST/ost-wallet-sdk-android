/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;

import org.json.JSONObject;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.ui.workflow.walletsetup.PinFragment;
import ost.com.demoapp.util.CommonUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkFlowPinFragment extends PinFragment {

    private OstPinAcceptInterface mOstPinAcceptInterface;
    private OnFragmentInteractionListener mListener;

    public static WorkFlowPinFragment newInstance(String heading) {
        return newInstance(heading, null);
    }

    public static WorkFlowPinFragment newInstance(String heading, String subHeading) {
        WorkFlowPinFragment fragment = new WorkFlowPinFragment();
        Bundle args = new Bundle();
        args.putString(HEADING, heading);
        args.putString(SUB_HEADING, subHeading);
        fragment.setArguments(args);
        return fragment;
    }

    public void setPinCallback(OstPinAcceptInterface ostPinAcceptInterface) {
        mOstPinAcceptInterface = ostPinAcceptInterface;
    }
    @Override
    protected void setListener(Context context) {
        if (context instanceof WorkFlowPinFragment.OnFragmentInteractionListener) {
            mListener = (WorkFlowPinFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected boolean onValidPin(String pin) {
        LogInUser logInUser = AppProvider.get().getCurrentUser();
        showProgress(true);
        AppProvider.get().getMappyClient().getLoggedInUserPinSalt(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (new CommonUtils().isValidResponse(jsonObject)){
                    try {
                        JSONObject userSaltObject = (JSONObject) new CommonUtils().parseResponseForResultType(jsonObject);
                        String userPinSalt = userSaltObject.getString("recovery_pin_salt");
                        UserPassphrase userPassphrase = new UserPassphrase(logInUser.getOstUserId(), pin, userPinSalt );
                        mOstPinAcceptInterface.pinEntered(userPassphrase);

                        //Close fragment by notifying DashBoard activity
                        mListener.popTopFragment();
                    } catch (Exception e){
                        Log.d("getPinSalt", "Exception in fetching Pin Salt.");
                        pinSaltNotFetched();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("getPinSalt", String.format("Error in fetching Pin Salt. %s", (null != throwable ? throwable.getMessage() : "")));
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
            mListener.invalidPin(0, null, AppProvider.get().getCurrentUser().getOstUserId(), mOstPinAcceptInterface);
        }
        goBack();
    }

    public interface OnFragmentInteractionListener {
        void popTopFragment();
        void invalidPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface);
    }
}