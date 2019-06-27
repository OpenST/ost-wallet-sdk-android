/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.walletsetup;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.ostwallet.R;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.ui.workflow.ChildFragmentStack;
import com.ost.ostwallet.util.ChildFragmentUtils;
import com.ost.ostwallet.util.DialogFactory;

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
    public static WalletSetUpFragment newInstance() {
        WalletSetUpFragment fragment = new WalletSetUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Add parameter if any
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_workflow_holder, container, false);

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
    public void showAddPin() {
        PinFragment fragment = PinFragment.newInstance("Add PIN", getResources().getString(R.string.pin_sub_heading_add_pin));
        fragment.showTermsLine = true;
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void showRetypePin() {
        PinFragment fragment = PinFragment.newInstance("Confirm PIN", getResources().getString(R.string.pin_sub_heading_confirm_pin));
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
            mWalletSetupPresenter.popBack();
            ChildFragmentUtils.goBack(this);
            return true;
        }
        return false;
    }

    public interface OnFragmentInteractionListener {

        void activateAcknowledged(long workflowId);

        void openWebView(String url);
    }
}