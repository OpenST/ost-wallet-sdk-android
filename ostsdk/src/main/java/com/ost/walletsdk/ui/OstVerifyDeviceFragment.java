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
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OstVerifyDeviceFragment} factory method to
 * create an instance of this fragment.
 */
public class OstVerifyDeviceFragment extends BottomSheetDialogFragment {

    OstVerifyDataInterface mOstVerifyDataInterface;
    private OstDevice mOstDevice;
    private ViewGroup mViewGroup;
    private OnFragmentInteractionListener mListener;
    private JSONObject mVerifyDeviceConfig = new JSONObject();

    public OstVerifyDeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener)context;
        } else {
            throw new RuntimeException("Activity Launching OstVerifyDeviceFragment does not implements OstVerifyDeviceFragment.OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewGroup = (ViewGroup) inflater.inflate(R.layout.ost_fragment_device_verify_data, container, true);

        TextView heading = (TextView) mViewGroup.findViewById(R.id.h2VerifyHeading);
        heading.setText(
                StringConfig.instance(mVerifyDeviceConfig.optJSONObject("lead_label")).getString()
        );

        TextView deviceAddress = (TextView) mViewGroup.findViewById(R.id.tv_address);
        deviceAddress.setText(mOstDevice.getAddress());

        ((Button)mViewGroup.findViewById(R.id.btnAcceptRequest)).setText(getPositiveButtonText());
        mViewGroup.findViewById(R.id.btnAcceptRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOstVerifyDataInterface.dataVerified();
                mListener.onDataVerified();
                dismissAllowingStateLoss();
            }
        });

        ((Button)mViewGroup.findViewById(R.id.btnDenyRequest)).setText(getNegativeButtonText());
        mViewGroup.findViewById(R.id.btnDenyRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOstVerifyDataInterface.cancelFlow();
                mListener.onDataRejected();
                dismissAllowingStateLoss();
            }
        });

        return mViewGroup;
    }

    public void setDataToVerify(OstDevice mDataToVerfiy) {
        this.mOstDevice = mDataToVerfiy;
    }

    public void setVerifyDataCallback(OstVerifyDataInterface ostVerifyDataInterface) {
        mOstVerifyDataInterface = ostVerifyDataInterface;
    }

    public void setStringConfig(JSONObject verifyDevice) {
        mVerifyDeviceConfig = verifyDevice;
    }

    private String getNegativeButtonText() {
        return StringConfig.instance(mVerifyDeviceConfig.optJSONObject("reject_button")).getString();
    }

    String getPositiveButtonText() {
        return StringConfig.instance(mVerifyDeviceConfig.optJSONObject("accept_button")).getString();
    }

    public interface OnFragmentInteractionListener {
        void onDataVerified();
        void onDataRejected();
    }
}