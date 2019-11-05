/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.entity.Transaction;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.util.WrapLinearLayoutManager;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstToken;

public class WalletFragment extends BaseFragment implements WalletView {

    private TextView mWalletBalance;
    private TextView mWalletUsdBalance;

    private WalletPresenter mWalletPresenter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mPullToRefresh;
    private Boolean paginationRequestSent = false;
    private LinearLayout mEmptyWalletLL;
    private WalletFragment.walletFragmentInteraction mListener;

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
        setDefaultText();
        mRecyclerView = view.findViewById(R.id.rv_transactions);
        mPullToRefresh = view.findViewById(R.id.pullToRefresh);
        AppBar appBar = AppBar.newInstance(getContext(),
                "Wallet",
                false);
        setUpAppBar(view, appBar);

        mWalletPresenter = WalletPresenter.newInstance();
        mWalletPresenter.attachView(this);

        final LinearLayoutManager layoutManager = new WrapLinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mWalletPresenter.getTransactionRecyclerViewAdapter());

        updateBalance("0.00", null);

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
                refreshWalletView();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WalletFragment.walletFragmentInteraction) {
            mListener = (WalletFragment.walletFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateBalance(String balance, String usdBalance) {
        mWalletBalance.setText(String.format("%s %s", balance, AppProvider.get().getCurrentEconomy().getTokenSymbol()));
        if(usdBalance != null){
            mWalletUsdBalance.setText(String.format("$ %s", usdBalance));
            mWalletUsdBalance.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        paginationRequestSent = false;
        mWalletPresenter.getTransactionRecyclerViewAdapter().notifyDataSetChanged();
        setDefaultText();
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

    @Override
    public void openTransactionView(Transaction transaction){
        if(null != mListener && null != transaction){
            String viewEndPoint = AppProvider.get().getCurrentEconomy().getViewApiEndpoint();
            OstToken token = OstSdk.getToken(AppProvider.get().getCurrentEconomy().getTokenId());
            String url = viewEndPoint + "transaction/tx-" + token.getChainId() + "-" + transaction.getTxnHash();
            mListener.openWebView(url);
        }
    }

    private void setDefaultText(){
        if(null != mEmptyWalletLL){
            if(!AppProvider.get().getCurrentUser().getOstUser().isActivated()){
                ((TextView)mEmptyWalletLL.findViewById(R.id.empty_wallet_text_tv1)).
                        setText(getResources().getString(R.string.wallet_being_setup));
                ((TextView)mEmptyWalletLL.findViewById(R.id.empty_wallet_text_tv2)).
                        setText(getResources().getString(R.string.wallet_setup_text));
            } else {
                ((TextView)mEmptyWalletLL.findViewById(R.id.empty_wallet_text_tv1)).
                        setText(getResources().getString(R.string.no_transactions_title));
                ((TextView)mEmptyWalletLL.findViewById(R.id.empty_wallet_text_tv2)).
                        setText(getResources().getString(R.string.no_transactions_text));
            }
        }
    }

    public void refreshWalletView(){
        if(null != mWalletPresenter){
            mWalletPresenter.updateBalance();
            mWalletPresenter.updateTransactionHistory(true);
        }
        if(null != mPullToRefresh){
            mPullToRefresh.setRefreshing(false);
        }
        if(null != mEmptyWalletLL){
            mEmptyWalletLL.setVisibility(View.VISIBLE);
        }
    }

    public interface walletFragmentInteraction{
        void openWebView(String url);
    }
}