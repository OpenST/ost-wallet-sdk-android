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
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.ui.workflow.walletsetup.PinFragment;

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
        UserPassphrase userPassphrase = new UserPassphrase(logInUser.getOstUserId(), pin, logInUser.getUserPinSalt() );
        showProgress(true);
        mOstPinAcceptInterface.pinEntered(userPassphrase);

        //Close fragment by notifying DashBoard activity
        mListener.popTopFragment();
        return true;
    }

    @Override
    public void goBack() {
        mOstPinAcceptInterface.cancelFlow();
        super.goBack();
    }

    public WorkFlowPinFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        void popTopFragment();
    }
}