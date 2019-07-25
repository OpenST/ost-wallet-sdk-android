/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui.walletsetup;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ost.com.ostsdkui.BaseFragment;
import ost.com.ostsdkui.ChildFragmentStack;
import ost.com.ostsdkui.R;
import ost.com.ostsdkui.util.ChildFragmentUtils;
import ost.com.ostsdkui.util.DialogFactory;

import static ost.com.ostsdkui.OstWorkFlowActivity.EXPIRED_AFTER_SECS;
import static ost.com.ostsdkui.OstWorkFlowActivity.SPENDING_LIMIT;
import static ost.com.ostsdkui.OstWorkFlowActivity.USER_ID;
import static ost.com.ostsdkui.OstWorkFlowActivity.WORKFLOW_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletSetUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletSetUpFragment extends BaseFragment implements SetUpView,
    PinFragment.OnFragmentInteractionListener,
        ChildFragmentStack {


    WalletSetUpPresenter mWalletSetupPresenter = WalletSetUpPresenter.getInstance();
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
            long workflowId = getArguments().getLong(WORKFLOW_ID);
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
        PinFragment fragment = PinFragment.newInstance("Create Pin",
                "Add a 6-digit PIN to secure your wallet.",
                "(PIN helps you recover your wallet if the phone is lost or stolen)", false);
        fragment.showTermsLine = true;
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void showRetypePin() {
        PinFragment fragment = PinFragment.newInstance("Confirm PIN",
                "If you forget your PIN, you cannot recover your wallet.",
                "(So please be sure to remember it)", true);
        fragment.showTermsLine = true;
        ChildFragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void gotoDashboard(long workflowId) {
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

        void activateAcknowledged(long workflowId);

        void openWebView(String url);
    }
}