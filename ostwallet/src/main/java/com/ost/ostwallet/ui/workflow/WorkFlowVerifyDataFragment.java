/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import com.ost.ostwallet.R;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.uicomponents.OstPrimaryButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkFlowVerifyDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkFlowVerifyDataFragment extends BaseFragment {

    OstVerifyDataInterface mOstVerifyDataInterface;
    private Object mDataToVerify;
    private ViewGroup mViewGroup;
    private View mVerifyDataView;

    public WorkFlowVerifyDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OstVerifyDeviceFragment.
     */
    public static WorkFlowVerifyDataFragment newInstance() {
        WorkFlowVerifyDataFragment fragment = new WorkFlowVerifyDataFragment();
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
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_work_flow_verify_data, container, true);

        ((Button)mViewGroup.findViewById(R.id.pbtn_verified)).setText(getPositiveButtonText());
        mViewGroup.findViewById(R.id.pbtn_verified).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOstVerifyDataInterface.dataVerified();
                showProgress(true, "Request processing...");
            }
        });
        mViewGroup.findViewById(R.id.pbtn_deny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        mVerifyDataView = getVerifyDataView();
        ((FrameLayout)mViewGroup.findViewById(R.id.fl_view_holder)).addView(mVerifyDataView);

        ((OstPrimaryButton) mViewGroup.findViewById(R.id.pbtn_verified)).setEnabled(enablePrimaryButton());

        AppBar appBar = AppBar.newInstance(getContext(), getTitle(), true);
        setUpAppBar(mViewGroup, appBar);
    }

    View getVerifyDataView() {
        return new View(getContext());
    }

    public Boolean enablePrimaryButton(){
        return true;
    }

    Object getVerifyData() {
        return mDataToVerify;
    }

    String getVerifyDataHeading() {
        return "Data";
    }

    String getPositiveButtonText() {
        return "Authorize";
    }

    String getTitle() {
        return "Verify Data";
    }

    @Override
    public void goBack() {
        mOstVerifyDataInterface.cancelFlow();
        super.goBack();
    }

    public void setDataToVerify(Object mDataToVerfiy) {
        this.mDataToVerify = mDataToVerfiy;
    }

    public void setVerifyDataCallback(OstVerifyDataInterface ostVerifyDataInterface) {
        mOstVerifyDataInterface = ostVerifyDataInterface;
    }

    public View refreshDataView(){
        Boolean enableButton = enablePrimaryButton();
        if(enableButton){
            ((TextView) mVerifyDataView.findViewById(R.id.tv_not_enough_balance)).setVisibility(View.GONE);
        } else {
            ((TextView) mVerifyDataView.findViewById(R.id.tv_not_enough_balance)).setVisibility(View.VISIBLE);
        }
        ((OstPrimaryButton) mViewGroup.findViewById(R.id.pbtn_verified)).setEnabled(enableButton);
        return mVerifyDataView;
    }
}
