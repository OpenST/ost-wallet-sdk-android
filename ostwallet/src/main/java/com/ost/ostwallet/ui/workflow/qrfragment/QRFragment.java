/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.qrfragment;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.sdkInteract.SdkInteract;
import com.ost.ostwallet.sdkInteract.WorkFlowListener;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.entity.LogInUser;
import com.ost.ostwallet.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QRFragment extends BaseFragment implements SdkInteract.FlowComplete, SdkInteract.FlowInterrupt {

    public QRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment WalletDetailsFragment.
     */
    public static QRFragment newInstance() {
        QRFragment fragment = new QRFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_qr, container, false);
        AppBar appBar = AppBar.newInstance(getContext(), "Device QR", true);
        setUpAppBar(viewGroup, appBar);

        LogInUser logInUser = AppProvider.get().getCurrentUser();
        Bitmap bitmap = OstSdk.getAddDeviceQRCode(logInUser.getOstUserId());
         ((ImageView)viewGroup.findViewById(R.id.iv_qr_view)).setImageBitmap(bitmap);

        ((TextView)viewGroup.findViewById(R.id.atv_device_address)).setText(logInUser.getOstUser().getCurrentDevice().getAddress());

        final WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        SdkInteract.getInstance().subscribe(workFlowListener.getId(), this);

        ((Button)viewGroup.findViewById(R.id.pbtn_check_device_status)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true, "Checking...");
                OstSdk.setupDevice(AppProvider.get().getCurrentUser().getOstUserId(),
                        AppProvider.get().getCurrentUser().getTokenId(),
                        true,
                        workFlowListener
                );
            }
        });

        return viewGroup;
    }

    @Override
    public void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        showFeedback();
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        showFeedback();
    }

    private void showFeedback() {
        showProgress(false);
        if (OstDevice.CONST_STATUS.AUTHORIZED
                .equalsIgnoreCase(
                        AppProvider.get().getCurrentUser().getOstUser().getCurrentDevice().getStatus()
                )) {
            showToastMessage("Device is Authorized", true);
        } else if (OstDevice.CONST_STATUS.AUTHORIZING
                .equalsIgnoreCase(
                        AppProvider.get().getCurrentUser().getOstUser().getCurrentDevice().getStatus()
                )) {
            showToastMessage("Device is still Authorizing", false);
        } else if (OstDevice.CONST_STATUS.REGISTERED
                .equalsIgnoreCase(
                        AppProvider.get().getCurrentUser().getOstUser().getCurrentDevice().getStatus()
                )) {
            showToastMessage("Device is still in Registered state", false);
        } else {
            showToastMessage("Device is still in InConsistent state", false);
        }
    }
}