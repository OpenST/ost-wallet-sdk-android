/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.walletdetails;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.customView.AppBar;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletDetailsFragment extends BaseFragment {

    public WalletDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment WalletDetailsFragment.
     */
    public static WalletDetailsFragment newInstance() {
        WalletDetailsFragment fragment = new WalletDetailsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_wallet_details, container, false);
        AppBar appBar = AppBar.newInstance(getContext(), "Wallet Details", true);
        setUpAppBar(viewGroup, appBar);
        LogInUser logInUser = AppProvider.get().getCurrentUser();

        ((EditText)viewGroup.findViewById(R.id.ost_user_id_edit)).setText(logInUser.getOstUserId());

        ((EditText)viewGroup.findViewById(R.id.ost_user_status_edit)).setText(logInUser.getOstUser().getStatus());

        ((EditText)viewGroup.findViewById(R.id.ost_token_id_edit)).setText(logInUser.getTokenId());

        ((EditText)viewGroup.findViewById(R.id.ost_user_token_holder_edit)).setText(logInUser.getOstUser().getTokenHolderAddress());

        ((EditText)viewGroup.findViewById(R.id.ost_user_device_address_edit)).setText(logInUser.getOstUser().getCurrentDevice().getAddress());

        ((EditText)viewGroup.findViewById(R.id.ost_user_device_status_edit)).setText(logInUser.getOstUser().getCurrentDevice().getStatus());

        ((EditText)viewGroup.findViewById(R.id.ost_user_device_manager_address_edit)).setText(logInUser.getOstUser().getDeviceManagerAddress());

        ((EditText)viewGroup.findViewById(R.id.ost_user_recovery_key_address_edit)).setText(logInUser.getOstUser().getRecoveryAddress());

        ((EditText)viewGroup.findViewById(R.id.ost_user_recovery_owner_address_edit)).setText(logInUser.getOstUser().getRecoveryOwnerAddress());

        ((EditText)viewGroup.findViewById(R.id.ost_user_platform_endpoint_edit)).setText(AppProvider.get().getCurrentEconomy().getSaasApiEndpoint());

        return viewGroup;
    }
}