/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.auth;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONException;
import org.json.JSONObject;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.entity.CurrentEconomy;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BasePresenter;
import ost.com.demoapp.util.CommonUtils;

class OnBoardingPresenter extends BasePresenter<OnBoardingView> implements
        SdkInteract.FlowComplete,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstOnBoardingPresenter";

    public static OnBoardingPresenter getInstance() {
        return new OnBoardingPresenter();
    }

    private OnBoardingPresenter() {

    }

    void createAccount(String userName, String password) {

        getMvpView().showProgress(true);
        AppProvider.get().getMappyClient().createAccount(userName, password, new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.d(LOG_TAG, jsonObject.toString());
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    try {
                        JSONObject loginUserObject = (JSONObject) new CommonUtils().parseResponseForResultType(jsonObject);
                        LogInUser logInUser = LogInUser.newInstance(loginUserObject);

                        AppProvider.get().setCurrentUser(logInUser);

                        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                        Log.i(LOG_TAG, String.format("Workflow id: %d", workFlowListener.getId()));
                        OstSdk.setupDevice(logInUser.getOstUserId(), logInUser.getTokenId(), workFlowListener);

                        SdkInteract.getInstance().subscribe(workFlowListener.getId(), OnBoardingPresenter.this);
                        SdkInteract.getInstance().subscribe(workFlowListener.getId(), OnBoardingPresenter.this);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    getMvpView().showError("Error while logging");
                    Log.e(LOG_TAG, "Error while logging");
                    getMvpView().showProgress(false);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().showProgress(false);
                Log.e(LOG_TAG, throwable.getMessage());
            }
        });
    }

    void logIn(String userName, String password) {
        getMvpView().showProgress(true);
        AppProvider.get().getMappyClient().logIn(userName, password, new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.d(LOG_TAG, jsonObject.toString());
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    try {
                        JSONObject loginUserObject = (JSONObject) new CommonUtils().parseResponseForResultType(jsonObject);
                        LogInUser logInUser = LogInUser.newInstance(loginUserObject);
                        AppProvider.get().setCurrentUser(logInUser);
                        WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                        Log.i(LOG_TAG, String.format("Workflow id: %d", workFlowListener.getId()));
                        OstSdk.setupDevice(logInUser.getOstUserId(), logInUser.getTokenId(), workFlowListener);
                        SdkInteract.getInstance().subscribe(workFlowListener.getId(), OnBoardingPresenter.this);
                        SdkInteract.getInstance().subscribe(workFlowListener.getId(), OnBoardingPresenter.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    getMvpView().showError("Error while logging");
                    Log.e(LOG_TAG, "Error while logging");
                    getMvpView().showProgress(false);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().showProgress(false);
                Log.e(LOG_TAG, throwable.getMessage());
            }
        });
    }

    void checkLoggedInUser() {
        getMvpView().showProgress(true);
        AppProvider.get().getMappyClient().getLoggedInUser(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    try {
                        JSONObject loginUserObject = (JSONObject) new CommonUtils().parseResponseForResultType(jsonObject);
                        LogInUser logInUser = LogInUser.newInstance(loginUserObject);
                        AppProvider.get().setCurrentUser(logInUser);
                        getMvpView().goToDashBoard();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(LOG_TAG, "Error while logging");
                    getMvpView().showProgress(false);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().showProgress(false);
                Log.e(LOG_TAG, null != throwable ? throwable.getMessage() : "");
            }
        });
    }

    void onScanEconomyResult(String returnedResult) throws JSONException {
        CurrentEconomy currentEconomy = CurrentEconomy.newInstance(returnedResult);
        AppProvider.get().setCurrentEconomy(currentEconomy);
        getMvpView().refreshToken();
    }

    @Override
    public void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.i(LOG_TAG, String.format("%d Flow Complete", workflowId));
        getMvpView().showProgress(false);
        getMvpView().goToDashBoard();
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Log.i(LOG_TAG, String.format("%d Flow Interrupt", workflowId));
        getMvpView().showProgress(false);
    }
}
