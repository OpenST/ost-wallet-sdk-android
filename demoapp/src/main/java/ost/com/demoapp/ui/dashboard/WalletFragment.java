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
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.ui.BaseFragment;

public class WalletFragment extends BaseFragment implements WalletView {

    private TextView mWalletBalance;
    private TextView mWalletUsdBalance;

    private WalletPresenter mWalletPresenter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mPullToRefresh;
    private Boolean paginationRequestSent = false;
    private LinearLayout mEmptyWalletLL;

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
        mWalletUsdBalance = view.findViewById(R.id.ptv_wallet_usd_balance);
        mEmptyWalletLL = view.findViewById(R.id.empty_wallet_text);
        mRecyclerView = view.findViewById(R.id.rv_transactions);
        mPullToRefresh = view.findViewById(R.id.pullToRefresh);
        AppBar appBar = AppBar.newInstance(getContext(),
                "Your Wallet",
                false);
        setUpAppBar(view, appBar);

        mWalletPresenter = WalletPresenter.newInstance();
        mWalletPresenter.attachView(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mWalletPresenter.getTransactionRecyclerViewAdapter());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!paginationRequestSent && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >=
                        totalItemCount && firstVisibleItemPosition >= 0) {
                    paginationRequestSent = true;
                    mWalletPresenter.updateTransactionHistory(false);
                }
            }
        });

        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWalletPresenter.updateBalance();
                mWalletPresenter.updateTransactionHistory(true);
                mPullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void updateBalance(String balance, String usdBalance) {
        mWalletBalance.setText(balance);
        if(usdBalance != null){
            mWalletUsdBalance.setText(String.format("≈ $ %s", usdBalance));
            mWalletUsdBalance.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        paginationRequestSent = false;
        mWalletPresenter.getTransactionRecyclerViewAdapter().notifyDataSetChanged();
        if(mWalletPresenter.getTransactionRecyclerViewAdapter().getItemCount() > 0){
            mEmptyWalletLL.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWalletPresenter.detachView();
        mWalletPresenter = null;
    }
}