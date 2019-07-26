/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.auth;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONException;
import org.json.JSONObject;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.entity.CurrentEconomy;
import com.ost.ostwallet.entity.LogInUser;
import com.ost.ostwallet.network.MappyNetworkClient;
import com.ost.ostwallet.sdkInteract.SdkInteract;
import com.ost.ostwallet.sdkInteract.WorkFlowListener;
import com.ost.ostwallet.ui.BasePresenter;
import com.ost.ostwallet.util.CommonUtils;

public class OnBoardingPresenter extends BasePresenter<OnBoardingView> implements
        SdkInteract.FlowComplete,
        SdkInteract.FlowInterrupt {

    private static final String LOG_TAG = "OstOnBoardingPresenter";
    private static final int MINIMUM_PWD_CHAR = 8;

    public static OnBoardingPresenter getInstance() {
        return new OnBoardingPresenter();
    }

    private OnBoardingPresenter() {

    }

    void createAccount(String userName, String password) {
        resetErrors();
        if (!assertEconomy()) {
            return;
        }
        if (!assertCredValidation(userName, password)) {
            return;
        }
        getMvpView().showProgress(true, "Creating account...");
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

                        getMvpView().showProgress(true, "Registering device...");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showErrorMessage(jsonObject);
                    Log.e(LOG_TAG, "Error while logging");
                    getMvpView().showProgress(false);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                showErrorMessage(null);
                getMvpView().showProgress(false);
                Log.e(LOG_TAG, throwable.getMessage());
            }
        });
    }

    private void showErrorMessage(JSONObject jsonObject) {
        String errorMsg = (null != jsonObject) ? jsonObject.optString("msg") : null;
        if (null != errorMsg) {
            getMvpView().showToastMessage(errorMsg, false);
        } else {
            getMvpView().showToastMessage("Could not connect to Mappy server. Please try after sometime.", false);
        }
    }

    private boolean assertCredValidation(String userName, String password) {
        boolean userNameValid = userName.matches("[a-zA-Z0-9]+");
        boolean passwordValid = password.matches("[a-zA-Z0-9]+") && password.length() >= MINIMUM_PWD_CHAR;
        if (!userNameValid) {
            getMvpView().showUsernameError("Username should be alpha numeric");
        }
        if (!passwordValid) {
            getMvpView().showPasswordError("Password should be alpha numeric and minimum 8 characters");
        }

        return userNameValid && passwordValid;
    }

    void logIn(String userName, String password) {
        resetErrors();
        if (!assertEconomy()) {
            return;
        }
        if (!assertCredValidation(userName, password)) {
            return;
        }
        getMvpView().showProgress(true, "Logging In");
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showErrorMessage(jsonObject);
                    Log.e(LOG_TAG, "Error while logging");
                    getMvpView().showProgress(false);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                showErrorMessage(null);
                getMvpView().showProgress(false);
                Log.e(LOG_TAG, null != throwable ? throwable.getMessage(): "Null Throwable");
            }
        });
    }

    void checkLoggedInUser() {
        getMvpView().showProgress(true, "Loading");
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
                    getMvpView().showEconomyChangeDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                getMvpView().showProgress(false);
                getMvpView().showEconomyChangeDialog();
                Log.e(LOG_TAG, null != throwable ? throwable.getMessage() : "Null Throwable");
            }
        });
    }

    void onScanEconomyResult(String returnedResult) throws JSONException {
        getMvpView().goBack();
        CurrentEconomy currentEconomy = null;
        try {
            currentEconomy = CurrentEconomy.newInstance(returnedResult);
        } catch (JSONException e) {
            //Nothing to be done
        }
        if (null == currentEconomy) {
            getMvpView().showToastMessage("Invalid Economy QR code! Try again.", false);
            return;
        }
        AppProvider.get().setCurrentEconomy(currentEconomy);
        getMvpView().refreshToken();
    }

    private void resetErrors() {
        getMvpView().showUsernameError(null);
        getMvpView().showPasswordError(null);
    }

    @Override
    public void flowComplete(String workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.i(LOG_TAG, String.format("%d Flow Complete", workflowId));
        getMvpView().showProgress(false);
        getMvpView().goToDashBoard();
    }

    @Override
    public void flowInterrupt(String workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Log.i(LOG_TAG, String.format("%d Flow Interrupt", workflowId));
        getMvpView().showProgress(false);
    }

    public boolean assertEconomy() {
        CurrentEconomy currentEconomy = AppProvider.get().getCurrentEconomy();
        if (null == currentEconomy) {
            getMvpView().scanForEconomy();
            return false;
        }
        return true;
    }

    public void refreshEconomyView(){
        getMvpView().refreshToken();
    }
}
