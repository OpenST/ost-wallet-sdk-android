/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ost.com.demoapp.R;
import ost.com.demoapp.customView.AppBar;
import ost.com.demoapp.ui.BaseFragment;

public class WalletFragment extends BaseFragment implements WalletView {

    private TextView mWalletBalance;

    private WalletPresenter mWalletPresenter = WalletPresenter.newInstance();
    public WalletFragment() {
    }

    public static WalletFragment newInstance() {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_wallet, container, false);

        mWalletBalance = view.findViewById(R.id.ptv_wallet_balance);
        AppBar appBar = AppBar.newInstance(getContext(),
                "Your Wallet",
                false);
        setUpAppBar(view, appBar);
        mWalletPresenter.attachView(this);
        return view;
    }

    @Override
    public void updateBalance(String balance) {
        mWalletBalance.setText(balance);
    }
}