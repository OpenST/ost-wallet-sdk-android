/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui.managedevices;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ost.com.ostsdkui.BaseFragment;
import ost.com.ostsdkui.R;
import ost.com.ostsdkui.uicomponents.AppBar;
import ost.com.ostsdkui.util.WrapLinearLayoutManager;

import static ost.com.ostsdkui.OstWorkFlowActivity.USER_ID;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ost.com.ostsdkui.managedevices.DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener}
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
    private String mUserId;
    private TextView mSubHeadingTextView;

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

    public static Fragment initiateRecoveryInstance(Bundle bundle) {
        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putString(ACTION_NAME, INITIATED_RECOVERY);
        args.putAll(bundle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAction = getArguments().getString(ACTION_NAME);
            mUserId = getArguments().getString(USER_ID);
            mDeviceListPresenter.setUserId(mUserId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ost_fragment_device_list, container, false);

        mHeadingTextView = ((TextView) view.findViewById(R.id.tv_heading));
        mHeadingTextView.setText(
                 "Device Recovery"
        );

        mSubHeadingTextView = ((TextView) view.findViewById(R.id.tv_sub_heading));
        mSubHeadingTextView.setText(
                "This is an authorized device, recovery applies only to cases where a user has no authorized device"
        );
        Context context = view.getContext();

        AppBar appBar = AppBar.newInstance(getContext(),
                true);
        setUpAppBar(view, appBar);

        mRecyclerView = view.findViewById(R.id.rv_devices);
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