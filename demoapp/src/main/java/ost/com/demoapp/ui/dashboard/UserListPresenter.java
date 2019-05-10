/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.dashboard;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.User;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.ui.BasePresenter;
import ost.com.demoapp.util.CommonUtils;

class UserListPresenter extends BasePresenter<UserListView> {
    private static final String LOG_TAG = "OstUserListPresenter";

    private UserListPresenter() {}

    public static UserListPresenter newInstance() {
        return new UserListPresenter();
    }

    private JSONObject nextPayload = new JSONObject();
    private Boolean hasMoreData = false;

    private List<User> userList = new ArrayList<>();

    @Override
    public void attachView(UserListView mvpView) {
        super.attachView(mvpView);
        updateUserList(true);
    }

    void updateUserList(Boolean clearList) {
        if(clearList){
            userList.clear();
            nextPayload = new JSONObject();
        } else if(!hasMoreData){
            return;
        }
        AppProvider.get().getMappyClient().getUserList(nextPayload, new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    try {
                        JSONObject dataJSONObject =  new CommonUtils().parseJSONData(jsonObject);
                        nextPayload = dataJSONObject.optJSONObject("meta");
                        hasMoreData = (nextPayload != null && !nextPayload.getJSONObject("next_page_payload").toString().equals("{}"));
                        JSONObject balancesJSONObject = dataJSONObject.optJSONObject("balances");
                        if (null == balancesJSONObject) balancesJSONObject = new JSONObject();

                        JSONArray userJSONArray = (JSONArray) new CommonUtils()
                                .parseResponseForResultType(jsonObject);


                        for (int i = 0; i < userJSONArray.length(); i++) {
                            JSONObject userJSONObject = userJSONArray.getJSONObject(i);
                            User user = User.newInstance(userJSONObject, balancesJSONObject);
                            userList.add(user);
                        }
                    } catch (JSONException e) {
                        //Exception not expected
                    }
                    getMvpView().notifyDataSetChanged();
                } else {
                    Log.e(LOG_TAG, String.format("Get Current User list response false: %s", jsonObject.toString()));
                    getMvpView().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(LOG_TAG, String.format("Get Current User list error: %s", throwable.toString()));
                getMvpView().notifyDataSetChanged();
            }
        });
    }

    void setUserList(List<User> userList) {
        this.userList = userList;
    }
}