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

import android.view.View;

import java.util.List;


class InitiateRecoveryRecyclerViewAdapter extends DeviceListRecyclerViewAdapter {
    private InitiateRecoveryRecyclerViewAdapter(List<Device> items, OnDeviceListInteractionListener listener) {
        super(items, listener);
    }

    public static InitiateRecoveryRecyclerViewAdapter newInstance(List<Device> deviceList, OnDeviceListInteractionListener deviceListPresenter) {
        return new InitiateRecoveryRecyclerViewAdapter(deviceList, deviceListPresenter);
    }

    @Override
    void handleView(final ViewHolder holder) {
        if (mCurrentDeviceAddress.equalsIgnoreCase(holder.mDevice.getDeviceAddress())) {
            holder.mActionButton.setVisibility(View.GONE);
        } else {
            holder.mActionButton.setText("Start Recovery");
            holder.mActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeviceSelectedForRecovery(holder.mDevice);
                }
            });
        }
    }
}