/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.walletsetup;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ost.com.demoapp.R;
import ost.com.demoapp.ui.BaseFragment;
import ost.com.demoapp.util.ChildFragmentUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletSetUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletSetUpFragment extends BaseFragment implements SetUpView,
    PinFragment.OnFragmentInteractionListener {


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
    // TODO: Rename and change types and number of parameters
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
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                PinFragment.newInstance("Add Pin", getResources().getString(R.string.pin_sub_heading_add_pin)),
                this);
    }

    @Override
    public void showRetypePin() {
        ChildFragmentUtils.addFragment(R.id.layout_container,
                PinFragment.newInstance("Confirm Pin", getResources().getString(R.string.pin_sub_heading_confirm_pin)),
                this);
    }

    @Override
    public void gotoDashboard(long workflowId) {
        mListener.activateAcknowledged(workflowId);
    }

    @Override
    public void onPinEntered(String pin) {
        mWalletSetupPresenter.onPinEntered(pin);
    }

    public interface OnFragmentInteractionListener {

        void activateAcknowledged(long workflowId);
    }
}