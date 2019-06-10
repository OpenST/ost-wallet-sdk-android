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

import android.view.View;

import java.util.List;

import com.ost.ostwallet.entity.Device;

class InitiateRecoveryRecyclerViewAdapter extends DeviceListRecyclerViewAdapter {
    private InitiateRecoveryRecyclerViewAdapter(List<Device> items, OnDeviceListInteractionListener listener) {
        super(items, listener);
    }

    public static InitiateRecoveryRecyclerViewAdapter newInstance(List<Device> deviceList, OnDeviceListInteractionListener deviceListPresenter) {
        return new InitiateRecoveryRecyclerViewAdapter(deviceList, deviceListPresenter);
    }

    @Override
    void handleView(ViewHolder holder) {
        if (mCurrentDeviceAddress.equalsIgnoreCase(holder.mDevice.getDeviceAddress())) {
            holder.mStatus.setVisibility(View.VISIBLE);
            holder.mActionButton.setVisibility(View.GONE);
            holder.mStatus.setText("This Device");
        } else {
            holder.mActionButton.setText("Select");
            holder.mActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeviceSelectedForRecovery(holder.mDevice);
                }
            });
        }
    }
}