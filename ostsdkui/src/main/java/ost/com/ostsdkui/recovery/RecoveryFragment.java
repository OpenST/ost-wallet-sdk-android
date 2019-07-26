/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui.recovery;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ost.com.ostsdkui.BaseFragment;
import ost.com.ostsdkui.R;
import ost.com.ostsdkui.WebViewFragment;
import ost.com.ostsdkui.util.ChildFragmentUtils;
import ost.com.ostsdkui.walletsetup.PinFragment;

import static ost.com.ostsdkui.OstWorkFlowActivity.USER_ID;
import static ost.com.ostsdkui.OstWorkFlowActivity.WORKFLOW_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecoveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecoveryFragment extends BaseFragment implements RecoveryView,
        PinFragment.OnFragmentInteractionListener {


    public static final String DEVICE_ADDRESS = "device_address";

    RecoveryPresenter recoveryPresenter = getPresenter();

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
            mDeviceAddress = getArguments().getString(DEVICE_ADDRESS);
            String userId = getArguments().getString(USER_ID);
            String workflowId = getArguments().getString(WORKFLOW_ID);
            recoveryPresenter.setArguments(userId, workflowId, mDeviceAddress);
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
    public void onPinEntered(String pin) {
        recoveryPresenter.onPinEntered(pin);
    }

    @Override
    public void openWebView(String url) {
        WebViewFragment fragment = WebViewFragment.newInstance(url);
        ChildFragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }
}