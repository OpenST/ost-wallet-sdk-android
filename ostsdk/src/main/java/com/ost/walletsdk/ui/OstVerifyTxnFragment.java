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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OstVerifyTxnFragment} factory method to
 * create an instance of this fragment.
 */
public class OstVerifyTxnFragment extends BottomSheetDialogFragment {

    OstVerifyDataInterface mOstVerifyDataInterface;
    private JSONObject mDataToVerify;
    private ViewGroup mViewGroup;
    private OnFragmentInteractionListener mListener;
    private JSONObject mVerifyTxnConfig = new JSONObject();
    private TextView mAmountInBt;
    private TextView mAmountInFiat;

    public OstVerifyTxnFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener)context;
        } else {
            throw new RuntimeException("Activity Launching OstVerifyTxnFragment does not implements OstVerifyTxnFragment.OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewGroup = (ViewGroup) inflater.inflate(R.layout.ost_fragment_txn_verify_data, container, true);

        TextView heading = (TextView) mViewGroup.findViewById(R.id.h2VerifyHeading);
        heading.setText(
                StringConfig.instance(mVerifyTxnConfig.optJSONObject("title_label")).getString()
        );

        TextView subHeading = (TextView) mViewGroup.findViewById(R.id.h3VerifyHeading);
        subHeading.setText(
                StringConfig.instance(mVerifyTxnConfig.optJSONObject("title_label")).getString()
        );

        mAmountInBt = (TextView) mViewGroup.findViewById(R.id.h1AmountInBt);

        mAmountInFiat = (TextView) mViewGroup.findViewById(R.id.h4AmountInFiat);

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

    public void setDataToVerify(JSONObject mDataToVerfiy) {
        this.mDataToVerify = mDataToVerfiy;
    }

    public void setVerifyDataCallback(OstVerifyDataInterface ostVerifyDataInterface) {
        mOstVerifyDataInterface = ostVerifyDataInterface;
    }

    public void setStringConfig(JSONObject verifyDevice) {
        mVerifyTxnConfig = verifyDevice;
    }

    private String getNegativeButtonText() {
        return StringConfig.instance(mVerifyTxnConfig.optJSONObject("reject_button")).getString();
    }

    String getPositiveButtonText() {
        return StringConfig.instance(mVerifyTxnConfig.optJSONObject("accept_button")).getString();
    }

    void updateBalance() {

        String pricerRule = mDataToVerify.optString(OstConstants.RULE_NAME);

        boolean isDirectTransfers = (pricerRule.equalsIgnoreCase("direct transfer"));

        JSONArray tokenHolderAddressesList = mDataToVerify.optJSONArray(OstConstants.TOKEN_HOLDER_ADDRESSES);
        JSONArray tokenHolderAmountsList = mDataToVerify.optJSONArray(OstConstants.AMOUNTS);
        JSONObject optionData = mDataToVerify.optJSONObject(OstConstants.TRANSACTION_OPTIONS);
        String currencySymbol = OstConfigs.getInstance().getPRICE_POINT_CURRENCY_SYMBOL();
        String mCurrencySign;
        if (null != optionData) {
            currencySymbol = optionData.optString(OstConstants.QR_CURRENCY_CODE, OstConfigs.getInstance().getPRICE_POINT_CURRENCY_SYMBOL());
            mCurrencySign = optionData.optString(OstConstants.QR_CURRENCY_SIGN, "$");
        }

        BigDecimal totalTransferAmount = new BigDecimal("0");

        for (int i=0; i<tokenHolderAddressesList.length(); i++) {
            String tokenHolderAmount = tokenHolderAmountsList.optString(i);
            totalTransferAmount = totalTransferAmount.add(new BigDecimal(tokenHolderAmount));
        }

        mAmountInBt.setText(String.format("%s", totalTransferAmount.toString()));
//        mAmountInFiat.setText(String.format(""))
//        if(isDirectTransfers){
//            ((TextView) viewGroup.findViewById(R.id.tv_balance)).setText(String.format(Locale.getDefault(), "Balance: %s %s",
//                    CommonUtils.convertWeiToTokenCurrency(AppProvider.get().getCurrentUser().getBalance()).toString(),
//                    AppProvider.get().getCurrentEconomy().getTokenSymbol()));
//        } else {
//            ((TextView) viewGroup.findViewById(R.id.tv_balance)).setText(String.format(Locale.getDefault(), "Balance: %s %s",
//                    mCurrencySign, CommonUtils.convertBTWeiToFiat(AppProvider.get().getCurrentUser().getBalance(), mPricePointData)));
//        }
    }
    public interface OnFragmentInteractionListener {
        void onDataVerified();
        void onDataRejected();
    }
}