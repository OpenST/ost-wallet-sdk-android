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

import android.view.View;

import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;

import java.util.List;


class InitiateRecoveryRecyclerViewAdapter extends DeviceListRecyclerViewAdapter {
    private InitiateRecoveryRecyclerViewAdapter(List<Device> items, OnDeviceListInteractionListener listener, String userId) {
        super(items, listener, userId);
    }

    public static InitiateRecoveryRecyclerViewAdapter newInstance(List<Device> deviceList, OnDeviceListInteractionListener deviceListPresenter, String userId) {
        return new InitiateRecoveryRecyclerViewAdapter(deviceList, deviceListPresenter, userId);
    }

    @Override
    void handleView(final ViewHolder holder) {
        if (mCurrentDeviceAddress.equalsIgnoreCase(holder.mDevice.getDeviceAddress())) {
            holder.mActionButton.setVisibility(View.GONE);
        } else {
            holder.mActionButton.setText(StringConfig.instance(mCellConfig.optJSONObject("action_button")).getString());
            holder.mActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeviceSelectedForRecovery(holder.mDevice);
                }
            });
        }
    }
}