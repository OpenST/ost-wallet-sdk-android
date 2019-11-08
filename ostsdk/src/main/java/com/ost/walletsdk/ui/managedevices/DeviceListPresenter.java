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

import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstJsonApi;
import com.ost.walletsdk.network.OstJsonApiCallback;
import com.ost.walletsdk.ui.BasePresenter;
import com.ost.walletsdk.ui.util.CommonUtils;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class DeviceListPresenter extends BasePresenter<DeviceListView> {
    private static final String LOG_TAG = "OstDeviceListPresenter";
    private static final int MINIMUM_DEVICES = 5;
    private String mUserId;
    private String currentDeviceAddress;
    private String mLoaderString = "Loading...";
    private boolean mRunningLoader = false;
    private boolean mClearDeviceList;

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
        currentDeviceAddress = OstUser.getById(mUserId).getCurrentDevice().getAddress();
        showLoaderProgress(true);
        updateDeviceList(true);
    }

    void updateDeviceList(boolean clearList) {
        if(httpRequestPending){
            return;
        }
        httpRequestPending = true;
        if(clearList){
            mClearDeviceList = true;
            nextPayload = new JSONObject();
        } else if(!hasMoreData){
            return;
        }
        showProgress(true);
        getMvpView().onInitialize();

        getDeviceList(new DeviceListCallback() {
            @Override
            public void done(List<Device> deviceList) {
                if (ostDeviceList.isEmpty()) {
                    ostDeviceList.add(Device.newInstance(new JSONObject()));
                }
            }
        });

    }

    private void getDeviceList(final DeviceListCallback callback) {
        Map<String ,Object> mapPayload = new HashMap<>();
        try {
            mapPayload = new CommonUtils().convertJsonToMap(nextPayload);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Exception while converting payload map", e);
        }
        OstJsonApi.getDeviceList(mUserId, mapPayload, new OstJsonApiCallback() {
            @Override
            public void onOstJsonApiSuccess(@Nullable JSONObject dataJSONObject) {
                if (null != dataJSONObject) {
                    try {
                        JSONObject meta = dataJSONObject.optJSONObject("meta");
                        if (null != meta) nextPayload = meta.optJSONObject("next_page_payload");


                        hasMoreData = (nextPayload != null && !nextPayload.toString().equals("{}"));
                        JSONArray deviceJSONArray = (JSONArray) dataJSONObject.get(dataJSONObject.getString(OstConstants.RESULT_TYPE));

                        //Clear list if new device request received
                        if (mClearDeviceList) {
                            ostDeviceList.clear();
                            mClearDeviceList = false;
                        }

                        //Populate authorized device in the list
                        for (int i = 0; i < deviceJSONArray.length(); i++) {
                            JSONObject deviceJSONObject = deviceJSONArray.getJSONObject(i);
                            Device device = Device.newInstance(deviceJSONObject);
                            if (device.isAuthorized() && !device.getDeviceAddress().equalsIgnoreCase(currentDeviceAddress)) {
                                ostDeviceList.add(device);
                            }
                        }

                        // Make request if did not get enough devices
                        if (hasMoreData && ostDeviceList.size() < MINIMUM_DEVICES) {
                            getDeviceList(callback);
                            return;
                        }

                        if (null != callback) callback.done(ostDeviceList);

                    } catch (JSONException e) {
                        //Exception not expected
                    }
                }
                httpRequestPending = false;
                showLoaderProgress(false);
                showProgress(false);
                getMvpView().notifyDataSetChanged();
            }

            @Override
            public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject response) {
                Log.e(LOG_TAG, String.format("Get Current User list error:"));
                showLoaderProgress(false);
                showProgress(false);
                getMvpView().notifyDataSetChanged();
                httpRequestPending = false;
            }
        });
    }
    private void showProgress(boolean show) {
        if (mRunningLoader) return;
        if (null != getMvpView()) getMvpView().setRefreshing(show);
    }

    private void showLoaderProgress(boolean show) {
        mRunningLoader = show;
        if (null != getMvpView()) getMvpView().showProgress(show, mLoaderString);
    }

    void setDeviceList(List<Device> ostDeviceList) {
        this.ostDeviceList = ostDeviceList;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public void setLoaderString(String initialLoaderString) {
        mLoaderString = initialLoaderString;
    }


    interface DeviceListCallback {
        void done(List<Device> deviceList);
    }
}