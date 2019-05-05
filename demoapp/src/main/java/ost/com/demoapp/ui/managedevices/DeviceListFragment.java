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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ost.com.demoapp.R;
import ost.com.demoapp.customView.AppBar;
import ost.com.demoapp.entity.Device;
import ost.com.demoapp.ui.BaseFragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DeviceListFragment extends BaseFragment implements DeviceListView,
        DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener {

    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mPullToRefresh;

    private DeviceListPresenter mDeviceListPresenter = DeviceListPresenter.newInstance();
    private DeviceListRecyclerViewAdapter mDeviceListRecyclerViewAdapter;
    private List<Device> mDeviceList;
    private TextView mHeadingTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceListFragment() {
    }


    public static DeviceListFragment newInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
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
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user_list, container, false);

        mHeadingTextView = ((TextView) view.findViewById(R.id.tv_heading));

        Context context = view.getContext();

        AppBar appBar = AppBar.newInstance(getContext(),
                "Manage Devices",
                true);
        setUpAppBar(view, appBar);

        mRecyclerView = view.findViewById(R.id.rv_users);
        mPullToRefresh = view.findViewById(R.id.pullToRefresh);

        mDeviceList = new ArrayList<Device>();
        mDeviceListPresenter.setDeviceList(mDeviceList);
        mDeviceListRecyclerViewAdapter = DeviceListRecyclerViewAdapter.newInstance(mDeviceList, this);

        mDeviceListPresenter.attachView(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(mDeviceListRecyclerViewAdapter);
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDeviceListPresenter.updateDeviceList();
                mPullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }


    private void updateDeviceCount(int count) {
        mHeadingTextView.setText(
                String.format(Locale.getDefault(), "You’ve %d authorized devices", count
                )
        );
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

    @Override
    public void onListViewInteraction(Device device) {
        mListener.onListFragmentInteraction(device);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Device device);
    }
}