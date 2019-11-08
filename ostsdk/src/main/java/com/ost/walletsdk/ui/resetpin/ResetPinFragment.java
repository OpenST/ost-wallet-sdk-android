/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.resetpin;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;
import com.ost.walletsdk.ui.ChildFragmentStack;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.util.ChildFragmentUtils;
import com.ost.walletsdk.ui.util.DialogFactory;
import com.ost.walletsdk.ui.walletsetup.PinFragment;

import static com.ost.walletsdk.ui.workflow.OstWorkFlowActivity.USER_ID;
import static com.ost.walletsdk.ui.workflow.OstWorkFlowActivity.WORKFLOW_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPinFragment extends BaseFragment implements ResetPinView,
        PinFragment.OnFragmentInteractionListener,
        ChildFragmentStack {


    ResetPinPresenter mResetPinPresenter = ResetPinPresenter.getInstance();
    private OnFragmentInteractionListener mListener;

    public ResetPinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static ResetPinFragment newInstance(Bundle bundle) {
        ResetPinFragment fragment = new ResetPinFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String userId = getArguments().getString(USER_ID);
            String workflowId = getArguments().getString(WORKFLOW_ID);
            mResetPinPresenter.setArguments(userId, workflowId);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException("Activity using ResetPinFragment should implement ResetPinFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.ost_fragment_workflow_holder, container, false);

        mResetPinPresenter.attachView(this);
        mResetPinPresenter.onCreateView();
        return view;
    }

    @Override
    public void showSetNewPin() {
        PinFragment fragment = PinFragment.newInstance("Add New Pin", getResources().getString(R.string.pin_sub_heading_add_pin), "", true);
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                fragment,
                this);
        fragment.contentConfig = ContentConfig.getInstance().getStringConfig("reset_pin").optJSONObject("set_new_pin");
    }

    @Override
    public void showRetypePin() {
        PinFragment fragment = PinFragment.newInstance("Confirm New Pin", getResources().getString(R.string.pin_sub_heading_confirm_pin),"", true);
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                fragment,
                this);
        fragment.contentConfig = ContentConfig.getInstance().getStringConfig("reset_pin").optJSONObject("confirm_new_pin");
    }

    @Override
    public void showEnterCurrentPin() {
        PinFragment fragment = PinFragment.newInstance("Enter Current Pin", getResources().getString(R.string.pin_sub_heading_current_pin),"", false);

        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                fragment,
                this);
        fragment.contentConfig = ContentConfig.getInstance().getStringConfig("reset_pin").optJSONObject("get_pin");
    }

    @Override
    public void gotoDashboard(String workflowId) {
        goBack();
    }

    @Override
    public void showPinErrorDialog() {
        Dialog dialog = DialogFactory.createSimpleOkErrorDialog(getContext(),
                null,
                "PIN doesn’t match.\n Please try again.",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mResetPinPresenter.resetResetPin();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onPostAuthentication() {
        getBaseActivity().getWorkflowLoader().onPostAuthentication();
    }

    @Override
    public void onPinEntered(String pin) {
        mResetPinPresenter.onPinEntered(pin);
    }

    @Override
    public void openWebView(String url) {
        mListener.openWebView(url);
    }

    @Override
    public boolean popBack() {
        if (mResetPinPresenter.haveBackStackFragment()) {
            mResetPinPresenter.goBackChildFragment();
            return true;
        }
        return false;
    }

    public interface OnFragmentInteractionListener {
        void openWebView(String url);
    }
}