/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.managedevices;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ost.com.demoapp.R;
import ost.com.demoapp.ui.BaseFragment;
import ost.com.demoapp.ui.workflow.entermnemonics.EnterMnemonicsFragment;
import ost.com.demoapp.ui.workflow.qrfragment.QRFragment;
import ost.com.demoapp.uicomponents.AppBar;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener}
 * interface.
 */
public class AuthorizeDeviceOptionsFragment extends BaseFragment implements View.OnClickListener {
    private OnAuthorizeDeviceOptionsFragmentListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AuthorizeDeviceOptionsFragment() {
    }


    public static AuthorizeDeviceOptionsFragment newInstance() {
        AuthorizeDeviceOptionsFragment fragment = new AuthorizeDeviceOptionsFragment();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAuthorizeDeviceOptionsFragmentListener) {
            mListener = (OnAuthorizeDeviceOptionsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAuthorizeDeviceOptionsFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_authorize_device_options, container, false);
        AppBar appBar = AppBar.newInstance(getContext(), "Authorize Device", true);
        setUpAppBar(view, appBar);

        ((View) view.findViewById(R.id.authorize_qr_btn)).setOnClickListener(this);

        ((View) view.findViewById(R.id.authorize_mnemonics_btn)).setOnClickListener(this);

        ((View) view.findViewById(R.id.authorize_pin_btn)).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.authorize_qr_btn: {
                mListener.launchFeatureFragment(QRFragment.newInstance());
                break;
            }
            case R.id.authorize_mnemonics_btn: {
                mListener.launchFeatureFragment(EnterMnemonicsFragment.newInstance());
                break;
            }
            case R.id.authorize_pin_btn: {
                mListener.launchFeatureFragment(DeviceListFragment.initiateRecoveryInstance());
                break;
            }
        }
    }

    public interface OnAuthorizeDeviceOptionsFragmentListener {
        void launchFeatureFragment(Fragment fragment);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}