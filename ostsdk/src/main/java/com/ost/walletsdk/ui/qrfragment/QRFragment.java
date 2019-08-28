/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.qrfragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;
import com.ost.walletsdk.ui.uicomponents.AppBar;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QRFragment extends BaseFragment {

    private String mUserId;
    private OnFragmentInteractionListener mListener;
    private JSONObject mContentConfig;

    public QRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QRFragment.
     */
    public static QRFragment newInstance(String mUserId) {
        QRFragment fragment = new QRFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setUserId(mUserId);
        return fragment;
    }

    private void setUserId(String userId) {
        mUserId = userId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QRFragment.OnFragmentInteractionListener) {
            mListener = (QRFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException("Activity using RecoveryFragment should implement ORFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.ost_fragment_qr, container, false);
        AppBar appBar = AppBar.newInstance(getContext(), false);
        setUpAppBar(viewGroup, appBar);

        ((TextView)viewGroup.findViewById(R.id.h1QRLabel)).setText(
                StringConfig.instance(mContentConfig.optJSONObject("title_label")).getString()
        );

        ((TextView)viewGroup.findViewById(R.id.h2QRLabel)).setText(
                StringConfig.instance(mContentConfig.optJSONObject("lead_label")).getString()
        );

        ImageView imageView = ((ImageView) viewGroup.findViewById(R.id.iv_qr_view));
        imageView.setImageBitmap(OstSdk.getAddDeviceQRCode(mUserId));

        TextView deviceAddressTextView = ((TextView) viewGroup.findViewById(R.id.atv_device_address));
        deviceAddressTextView.setText(OstSdk.getUser(mUserId).getCurrentDevice().getAddress());

        Button checkStatusButton = ((Button) viewGroup.findViewById(R.id.pbtn_check_device_status));
        checkStatusButton.setText(
                StringConfig.instance(mContentConfig.optJSONObject("action_button")).getString()
        );
        checkStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCheckDevice();
            }
        });

        return viewGroup;
    }

    public void setContentConfig(JSONObject contentConfig) {
        mContentConfig = contentConfig;
    }

    public interface OnFragmentInteractionListener {
        void onCheckDevice();
    }
}