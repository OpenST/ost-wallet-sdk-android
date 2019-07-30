/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.managedevices;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.models.entities.OstDevice;

import java.util.List;
import java.util.Locale;


/**
 * {@link RecyclerView.Adapter} that can display Devices and makes a call to the
 * specified {@link OnDeviceListInteractionListener}.
 */
public class DeviceListRecyclerViewAdapter extends RecyclerView.Adapter<DeviceListRecyclerViewAdapter.ViewHolder> {

    private final List<Device> mValues;
    final OnDeviceListInteractionListener mListener;
    final String mCurrentDeviceAddress;

    DeviceListRecyclerViewAdapter(List<Device> items, OnDeviceListInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mCurrentDeviceAddress = "";//AppProvider.get().getCurrentUser().getOstUser().getCurrentDevice().getAddress();
    }

    public static DeviceListRecyclerViewAdapter newInstance(List<Device> deviceList, OnDeviceListInteractionListener deviceListPresenter) {
        return new DeviceListRecyclerViewAdapter(deviceList, deviceListPresenter);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ost_view_device, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mDevice = mValues.get(position);


        holder.mUserName.setText(String.format(Locale.getDefault(), "Device %d", position + 1));
        holder.mAddress.setText(holder.mDevice.getDeviceAddress());
        holder.mActionButton.setVisibility(View.VISIBLE);
        handleView(holder);
    }

    void handleView(final ViewHolder holder) {
        String status = holder.mDevice.getStatus();
        if (OstDevice.CONST_STATUS.AUTHORIZED.equalsIgnoreCase(status)) {
            holder.mActionButton.setText("Start Recovery");
            holder.mActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeviceSelectToRevoke(holder.mDevice);
                }
            });
        }

        if (OstDevice.CONST_STATUS.RECOVERING.equalsIgnoreCase(status)) {
            holder.mActionButton.setText("Abort Recovery");
            holder.mActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeviceSelectedToAbortRecovery(holder.mDevice);
                }
            });
        } else if (mCurrentDeviceAddress.equalsIgnoreCase(holder.mDevice.getDeviceAddress())) {
            holder.mActionButton.setVisibility(View.GONE);
        }

        if (OstDevice.CONST_STATUS.REVOKING.equalsIgnoreCase(status)) {
            holder.mActionButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private final ImageView mImageView;
        private final TextView mUserName;
        private final TextView mAddress;
        final Button mActionButton;

        public Device mDevice;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.iv_device_image);
            mUserName = (TextView) view.findViewById(R.id.tv_device_name);
            mAddress = (TextView) view.findViewById(R.id.tv_address);
            mActionButton = (Button) view.findViewById(R.id.btn_list_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }

    public interface OnDeviceListInteractionListener {
        void onDeviceSelectToRevoke(Device device);
        void onDeviceSelectedForRecovery(Device device);
        void onDeviceSelectedToAbortRecovery(Device device);
    }
}