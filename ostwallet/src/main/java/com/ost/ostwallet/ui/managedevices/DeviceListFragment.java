/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.managedevices;

import android.content.Context;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ost.ostwallet.R;
import com.ost.ostwallet.entity.Device;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.util.WrapLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.ost.ostwallet.ui.managedevices.DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener}
 * interface.
 */
public class DeviceListFragment extends BaseFragment implements DeviceListView {

    private static final String ACTION_NAME = "action_name";
    private static final String INITIATED_RECOVERY = "initiate_recovery";
    private static final String MANAGE_DEVICE = "manage_device";

    private DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mPullToRefresh;

    private DeviceListPresenter mDeviceListPresenter = DeviceListPresenter.newInstance();
    private DeviceListRecyclerViewAdapter mDeviceListRecyclerViewAdapter;
    private List<Device> mDeviceList;
    private TextView mHeadingTextView;
    private String mAction;
    private Boolean paginationRequestSent = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceListFragment() {
    }


    public static DeviceListFragment manageDeviceInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putString(ACTION_NAME, MANAGE_DEVICE);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment initiateRecoveryInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putString(ACTION_NAME, INITIATED_RECOVERY);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAction = getArguments().getString(ACTION_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user_list, container, false);

        mHeadingTextView = ((TextView) view.findViewById(R.id.tv_heading));
        mHeadingTextView.setText(
                String.format(Locale.getDefault(), "This is a list of all the devices that are authorized to access your wallet.")
        );

        Context context = view.getContext();

        AppBar appBar = AppBar.newInstance(getContext(),
                "View Devices",
                true);
        setUpAppBar(view, appBar);

        mRecyclerView = view.findViewById(R.id.rv_users);
        mPullToRefresh = view.findViewById(R.id.pullToRefresh);

        mDeviceList = new ArrayList<Device>();
        mDeviceListPresenter.setDeviceList(mDeviceList);
        mDeviceListRecyclerViewAdapter = MANAGE_DEVICE.equalsIgnoreCase(mAction)
                ? DeviceListRecyclerViewAdapter.newInstance(mDeviceList ,mListener)
                : InitiateRecoveryRecyclerViewAdapter.newInstance(mDeviceList, mListener);

        mDeviceListPresenter.attachView(this);
        final LinearLayoutManager layoutManager = new WrapLinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mDeviceListRecyclerViewAdapter);

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
                    mDeviceListPresenter.updateDeviceList(false);
                }
            }
        });

        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDeviceListPresenter.updateDeviceList(true);
                mPullToRefresh.setRefreshing(false);
                paginationRequestSent = false;
            }
        });
        return view;
    }


    private void updateDeviceCount(int count) {

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener) {
            mListener = (DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void notifyDataSetChanged() {
        mDeviceListRecyclerViewAdapter.notifyDataSetChanged();
        updateDeviceCount(mDeviceListRecyclerViewAdapter.getItemCount());
    }
}