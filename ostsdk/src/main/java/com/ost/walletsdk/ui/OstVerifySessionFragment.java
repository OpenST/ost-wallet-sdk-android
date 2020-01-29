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


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import com.ost.walletsdk.models.entities.OstSession;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.CommonUtils;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONObject;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OstVerifySessionFragment} factory method to
 * create an instance of this fragment.
 */
public class OstVerifySessionFragment extends BottomSheetDialogFragment {

    OstVerifyDataInterface mOstVerifyDataInterface;
    private OstSession mOstSession;
    private ViewGroup mViewGroup;
    private OnFragmentInteractionListener mListener;
    private JSONObject mVerifySessionObject = new JSONObject();
    private TextView mSpendingAmount;
    private TextView mExpiryTime;
    private String mUserId;
    private TextView mSessionAddress;

    public OstVerifySessionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener)context;
        } else {
            throw new RuntimeException("Activity Launching OstVerifySessionFragment does not implements OstVerifySessionFragment.OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewGroup = (ViewGroup) inflater.inflate(R.layout.ost_fragment_session_verify_data, container, true);

        TextView heading = (TextView) mViewGroup.findViewById(R.id.h2VerifyHeading);
        heading.setText(
                StringConfig.instance(mVerifySessionObject.optJSONObject("lead_label")).getString()
        );

        mSessionAddress = (TextView) mViewGroup.findViewById(R.id.tv_address);

        mSpendingAmount = (TextView) mViewGroup.findViewById(R.id.tv_spending_amount);

        mExpiryTime = (TextView) mViewGroup.findViewById(R.id.tv_time);

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

        updateView();

        return mViewGroup;
    }

    public void setDataToVerify(OstSession ostSession) {
        this.mOstSession = ostSession;
    }

    public void setVerifyDataCallback(OstVerifyDataInterface ostVerifyDataInterface) {
        mOstVerifyDataInterface = ostVerifyDataInterface;
    }

    public void setStringConfig(JSONObject verifyDevice) {
        mVerifySessionObject = verifyDevice;
    }

    private String getNegativeButtonText() {
        return StringConfig.instance(mVerifySessionObject.optJSONObject("reject_button")).getString();
    }

    String getPositiveButtonText() {
        return StringConfig.instance(mVerifySessionObject.optJSONObject("accept_button")).getString();
    }

    void updateView() {
        String sessionAddress = mOstSession.getAddress();
        String spendingLimit = mOstSession.getSpendingLimit();
        String expiryTimestamp = mOstSession.getExpirationTimestamp();
        mSessionAddress.setText(sessionAddress);

        String spendingLimitInBt = new CommonUtils().convertWeiToTokenCurrency(mUserId, spendingLimit);
        mSpendingAmount.setText(spendingLimitInBt);

        String formattedExpiryDate = DateFormat.format("dd/MM/yyyy hh:mm:ss", new Date(Long.parseLong(expiryTimestamp) * 1000)).toString();
        mExpiryTime.setText( formattedExpiryDate );
    }

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public interface OnFragmentInteractionListener {
        void onDataVerified();
        void onDataRejected();
    }
}