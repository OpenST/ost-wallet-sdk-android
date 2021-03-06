/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.recovery;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.ostwallet.R;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.ui.dashboard.WebViewFragment;
import com.ost.ostwallet.ui.workflow.walletsetup.PinFragment;
import com.ost.ostwallet.util.ChildFragmentUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecoveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecoveryFragment extends BaseFragment implements RecoveryView,
        PinFragment.OnFragmentInteractionListener {


    static final String DEVICE_ADDRESS = "device_address";

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_workflow_holder, container, false);

        recoveryPresenter.attachView(this);
        recoveryPresenter.setDeviceAddress(mDeviceAddress);
        recoveryPresenter.onCreateView();
        return view;
    }

    @Override
    public void showEnterPin() {
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                PinFragment.newInstance("Enter PIN", getResources().getString(R.string.pin_sub_heading_get_pin)),
                this);
    }

    @Override
    public void gotoDashboard(long workflowId) {
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