/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.recovery;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;
import com.ost.walletsdk.ui.util.ChildFragmentUtils;
import com.ost.walletsdk.ui.walletsetup.PinFragment;

import org.json.JSONObject;

import static com.ost.walletsdk.ui.workflow.OstWorkFlowActivity.USER_ID;
import static com.ost.walletsdk.ui.workflow.OstWorkFlowActivity.WORKFLOW_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecoveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecoveryFragment extends BaseFragment implements RecoveryView,
        PinFragment.OnFragmentInteractionListener {


    public static final String DEVICE_ADDRESS = "device_address";
    public static final String SHOW_BACK_BUTTON = "show_back_button";

    RecoveryPresenter recoveryPresenter = getPresenter();
    boolean mShowBackButton = true;
    private OnFragmentInteractionListener mListener;

    public RecoveryPresenter getPresenter() {
        return AbortRecoveryPresenter.getInstance();
    }

    private String mDeviceAddress;

    public RecoveryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static RecoveryFragment newInstance(String deviceAddress) {
        RecoveryFragment fragment = new RecoveryFragment();
        Bundle args = new Bundle();
        args.putString(DEVICE_ADDRESS, deviceAddress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDeviceAddress = getArguments().getString(DEVICE_ADDRESS);
            String userId = getArguments().getString(USER_ID);
            String workflowId = getArguments().getString(WORKFLOW_ID);
            mShowBackButton = getArguments().getBoolean(SHOW_BACK_BUTTON);
            recoveryPresenter.setArguments(userId, workflowId, mDeviceAddress);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecoveryFragment.OnFragmentInteractionListener) {
            mListener = (RecoveryFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException("Activity using RecoveryFragment should implement RecoveryFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.ost_fragment_workflow_holder, container, false);

        recoveryPresenter.attachView(this);
        recoveryPresenter.onCreateView();
        return view;
    }

    @Override
    public void showEnterPin() {
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                PinFragment.newInstance("Recover access to OS Tokens",
                        "Enter your 6-digit PIN to access to your OS tokens"),
                this);
    }

    @Override
    public void gotoDashboard(String workflowId) {
        goBack();
    }

    @Override
    public void onPostAuthentication(JSONObject contentConfig) {
        getBaseActivity().getWorkflowLoader().onPostAuthentication(contentConfig);
    }

    @Override
    public void onPinEntered(String pin) {
        recoveryPresenter.onPinEntered(pin);
    }

    @Override
    public void openWebView(String url) {
        mListener.openWebView(url);
    }

    public interface OnFragmentInteractionListener {
        void openWebView(String url);
    }
}