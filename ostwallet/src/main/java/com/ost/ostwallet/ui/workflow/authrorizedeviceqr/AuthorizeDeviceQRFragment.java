/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.authrorizedeviceqr;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.ostwallet.R;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.ui.qrscanner.QRScannerFragment;
import com.ost.ostwallet.util.ChildFragmentUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuthorizeDeviceQRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthorizeDeviceQRFragment extends BaseFragment implements AuthorizeDeviceQRView,
        QRScannerFragment.OnFragmentInteractionListener {


    AuthorizeDeviceQRPresenter mAuthorizeDeviceQRPresenter = AuthorizeDeviceQRPresenter.getInstance();

    public AuthorizeDeviceQRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static AuthorizeDeviceQRFragment newInstance() {
        AuthorizeDeviceQRFragment fragment = new AuthorizeDeviceQRFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workflow_holder, container, false);

        mAuthorizeDeviceQRPresenter.attachView(this);
        mAuthorizeDeviceQRPresenter.onCreateView();
        return view;
    }

    @Override
    public void gotoDashboard(long workflowId) {
        goBack();
    }

    @Override
    public void launchQRScanner() {
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                QRScannerFragment.newInstance("Scan QR", getResources().getString(R.string.qr_sub_heading_authorize_scan)),
                this);
    }

    @Override
    public void onResultString(Intent resultString) {
        mAuthorizeDeviceQRPresenter.processQRResult(resultString);
    }
}