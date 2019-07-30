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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import ost.com.ostsdkui.util.CommonUtils;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.network.OstJsonApi;
import com.ost.walletsdk.network.OstJsonApiCallback;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ost.com.ostsdkui.BasePresenter;

class DeviceListPresenter extends BasePresenter<DeviceListView> {
    private static final String LOG_TAG = "OstDeviceListPresenter";
    private String mUserId;

    private DeviceListPresenter() {}

    public static DeviceListPresenter newInstance() {
        return new DeviceListPresenter();
    }

    private JSONObject nextPayload = new JSONObject();
    private Boolean hasMoreData = false;
    private Boolean httpRequestPending = false;

    private List<Device> ostDeviceList = new ArrayList<>();

    @Override
    public void attachView(DeviceListView mvpView) {
        super.attachView(mvpView);
        updateDeviceList(true);
    }

    void updateDeviceList(Boolean clearList) {
        if(httpRequestPending){
            return;
        }
        if(clearList){
            ostDeviceList.clear();
            nextPayload = new JSONObject();
        } else if(!hasMoreData){
            return;
        }
        httpRequestPending = true;

        Map<String ,Object> mapPayload = new HashMap<>();
        try {
            mapPayload = new CommonUtils().convertJsonToMap(nextPayload);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Exception while converting payload map", e);
        }
        showProgress(true);
        OstJsonApi.getDeviceList(mUserId, mapPayload, new OstJsonApiCallback() {
            @Override
            public void onOstJsonApiSuccess(@Nullable JSONObject dataJSONObject) {
                showProgress(false);
                try {
                    nextPayload = dataJSONObject.optJSONObject("meta");
                    hasMoreData = (nextPayload != null && !nextPayload.getJSONObject("next_page_payload").toString().equals("{}"));
                    JSONArray deviceJSONArray = (JSONArray) dataJSONObject.get(dataJSONObject.getString(OstConstants.RESULT_TYPE));

                    for (int i = 0; i < deviceJSONArray.length(); i++) {
                        JSONObject deviceJSONObject = deviceJSONArray.getJSONObject(i);
                        Device device = Device.newInstance(deviceJSONObject);
                        if (device.isAuthorized()) {
                            ostDeviceList.add(device);
                        }
                    }
                } catch (JSONException e) {
                    //Exception not expected
                }
                getMvpView().notifyDataSetChanged();

                httpRequestPending = false;
            }

            @Override
            public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) {
                Log.e(LOG_TAG, String.format("Get Current User list error:"));
                showProgress(false);
                getMvpView().notifyDataSetChanged();
                httpRequestPending = false;
            }
        });
    }

    private void showProgress(boolean show) {
        if (null != getMvpView()) getMvpView().showProgress(show);
    }

    void setDeviceList(List<Device> ostDeviceList) {
        this.ostDeviceList = ostDeviceList;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }
}