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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ost.walletsdk.models.entities.OstDevice;

import java.util.List;

import ost.com.demoapp.R;
import ost.com.demoapp.entity.User;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link OnDeviceListInteractionListener}.
 */
public class DeviceListRecyclerViewAdapter extends RecyclerView.Adapter<DeviceListRecyclerViewAdapter.ViewHolder> {

    private final List<OstDevice> mValues;
    private final OnDeviceListInteractionListener mListener;

    private DeviceListRecyclerViewAdapter(List<OstDevice> items, OnDeviceListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public static DeviceListRecyclerViewAdapter newInstance(List<OstDevice> deviceList, OnDeviceListInteractionListener deviceListPresenter) {
        return new DeviceListRecyclerViewAdapter(deviceList, deviceListPresenter);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mDevice = mValues.get(position);


        holder.mUserName.setText(holder.mDevice.getAddress());
        holder.mStatus.setText(holder.mDevice.getApiSignerAddress());
        holder.mSendButton.setText(holder.mDevice.getStatus());
        holder.mSendButton.setVisibility(View.VISIBLE);
        String status = holder.mDevice.getStatus();
        if (OstDevice.CONST_STATUS.AUTHORIZED
                .equalsIgnoreCase(status) ||
                OstDevice.CONST_STATUS.RECOVERING
                        .equalsIgnoreCase(status)) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListViewInteraction(holder.mDevice);
                }
            });
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
        private final TextView mStatus;
        private final Button mSendButton;

        public OstDevice mDevice;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.iv_user_image);
            mUserName = (TextView) view.findViewById(R.id.tv_user_name);
            mStatus = (TextView) view.findViewById(R.id.tv_status);
            mSendButton = (Button) view.findViewById(R.id.btn_send_token);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }

    public interface OnDeviceListInteractionListener {
        void onListViewInteraction(OstDevice device);
    }
}