/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.logging;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.entity.OstLogEvent;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.util.WrapLinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Log Events.
 */
public class WalletEventFragment extends BaseFragment {

    private List<OstLogEvent> mLogEventList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mPullToRefresh;
    private WalletEventRecyclerViewAdapter mRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WalletEventFragment() {
    }

    public static WalletEventFragment newInstance() {
        WalletEventFragment fragment = new WalletEventFragment();
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
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_event_list, container, false);

        AppBar appBar = AppBar.newInstance(getContext(),
                "Wallet Events",
                true);
        setUpAppBar(viewGroup, appBar);

        mRecyclerView = viewGroup.findViewById(R.id.rv_events);
        mPullToRefresh = viewGroup.findViewById(R.id.pullToRefresh);
        mRecyclerView.setLayoutManager(new WrapLinearLayoutManager(getContext()));
        mRecyclerViewAdapter = new WalletEventRecyclerViewAdapter(mLogEventList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWalletEventList();
                mPullToRefresh.setRefreshing(false);
            }
        });
        updateWalletEventList();
        return viewGroup;
    }

    private void updateWalletEventList() {
        OstLogEvent[] ostLogEvents = AppProvider.get().getDBLogger().getWalletEvents(50);
        mLogEventList.clear();
        mLogEventList.addAll(Arrays.asList(ostLogEvents));
        mRecyclerViewAdapter.notifyDataSetChanged();
    }
}
