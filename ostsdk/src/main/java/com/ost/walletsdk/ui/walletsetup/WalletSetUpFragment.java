/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.walletsetup;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;
import com.ost.walletsdk.ui.ChildFragmentStack;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.util.ChildFragmentUtils;
import com.ost.walletsdk.ui.util.DialogFactory;

import org.json.JSONObject;

import static com.ost.walletsdk.ui.OstWorkFlowActivity.EXPIRED_AFTER_SECS;
import static com.ost.walletsdk.ui.OstWorkFlowActivity.SPENDING_LIMIT;
import static com.ost.walletsdk.ui.OstWorkFlowActivity.USER_ID;
import static com.ost.walletsdk.ui.OstWorkFlowActivity.WORKFLOW_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletSetUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletSetUpFragment extends BaseFragment implements SetUpView,
    PinFragment.OnFragmentInteractionListener,
        ChildFragmentStack {


    WalletSetUpPresenter mWalletSetupPresenter = WalletSetUpPresenter.getInstance();
    JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("activate_user");
    private OnFragmentInteractionListener mListener;

    public WalletSetUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WalletSetUpFragment.
     */
    public static WalletSetUpFragment newInstance(Bundle bundle) {
        WalletSetUpFragment fragment = new WalletSetUpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String userId = getArguments().getString(USER_ID);
            String workflowId = getArguments().getString(WORKFLOW_ID);
            long expiredAfterSecs = getArguments().getLong(EXPIRED_AFTER_SECS, 100000);
            String spendingLimit = getArguments().getString(SPENDING_LIMIT);
            mWalletSetupPresenter.setArguments(userId, workflowId, expiredAfterSecs, spendingLimit);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.ost_fragment_workflow_holder, container, false);

        mWalletSetupPresenter.attachView(this);
        mWalletSetupPresenter.onCreateView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mWalletSetupPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWalletSetupPresenter.onPause();
    }

    @Override
    public void showAddPin() {
        PinFragment fragment = PinFragment.newInstance("Create PIN",
                "Add a 6-digit PIN to secure your wallet",
                "PIN helps to recover your wallet if your phone is lost or stolen", false);
        fragment.showTermsLine = true;
        fragment.contentConfig = contentConfig.optJSONObject("create_pin");
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void showRetypePin() {
        PinFragment fragment = PinFragment.newInstance("Confirm PIN",
                "If you forget your PIN, you cannot recover your wallet",
                "So please be sure to remember it", true);
        fragment.showTermsLine = true;
        fragment.contentConfig = contentConfig.optJSONObject("confirm_pin");
        ChildFragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void gotoDashboard(String workflowId) {
        mListener.activateAcknowledged(workflowId);
    }

    @Override
    public void showPinErrorDialog() {
        Dialog dialog = DialogFactory.createSimpleOkErrorDialog(getContext(),
                "PIN doesn’t match.\n Please try again.",
                "",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mWalletSetupPresenter.resetWalletSetUp();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    public Activity getCurrentActivity() {
        return getActivity();
    }

    @Override
    public void onPinEntered(String pin) {
        mWalletSetupPresenter.onPinEntered(pin);
    }

    @Override
    public void openWebView(String url) {
        mListener.openWebView(url);
    }

    @Override
    public boolean popBack() {
        if (this.getChildFragmentManager().getBackStackEntryCount() > 1) {
            mWalletSetupPresenter.resetWalletSetUp();
            return true;
        }
        return false;
    }

    public interface OnFragmentInteractionListener {

        void activateAcknowledged(String workflowId);

        void openWebView(String url);
    }
}