/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.util;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Keys;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.entity.LogInUser;

public class CommonUtils {
    private static final String LOG_TAG = "OstCommonUtils";
    private static final String DATA = "data";

    public CommonUtils() {
    }

    public JSONArray listToJSONArray(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jsonArray.put(list.get(i));
        }
        return jsonArray;
    }

    public List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "jsonArray to list exception", e);
        }
        return list;
    }

    public List<String> toCheckSumAddresses(List<String> addressList) {
        for (int i = 0; i < addressList.size(); i++) {
            String address = Keys.toChecksumAddress(addressList.get(i));
            addressList.set(i, address);
        }
        return addressList;
    }

    private static final byte[] nonSecret = ("LETS_CLEAR_BYTES" + String.valueOf((int) (System.currentTimeMillis()))).getBytes();

    public static void clearBytes(byte[] secret) {
        if (null == secret) {
            return;
        }
        for (int i = 0; i < secret.length; i++) {
            secret[i] = nonSecret[i % nonSecret.length];
        }
    }

    public String parseStringResponseForKey(JSONObject jsonObject, String key) {
        try {
            JSONObject resultType = (JSONObject) parseResponseForResultType(jsonObject);
            return resultType.getString(key);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Exception");
        }
        return null;
    }

    public boolean isValidResponse(JSONObject jsonObject) {
        try {
            if (jsonObject.getBoolean(OstConstants.RESPONSE_SUCCESS)) {
                return true;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Exception");
        }
        return false;
    }

    public Object parseResponseForResultType(JSONObject jsonObject) throws JSONException {
        if (!isValidResponse(jsonObject)) {
            Log.e(LOG_TAG, "JSON response false");
            return null;
        }
        JSONObject jsonData = jsonObject.getJSONObject(OstConstants.RESPONSE_DATA);
        return jsonData.get(jsonData.getString(OstConstants.RESULT_TYPE));
    }

    public Object parseObjectResponseForKey(JSONObject jsonObject, String key) {
        try {
            JSONObject resultType = (JSONObject) parseResponseForResultType(jsonObject);
            return resultType.get(key);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Exception");
        }
        return null;
    }

    public JSONObject parseJSONData(JSONObject jsonObject) {
        try {
            return jsonObject.getJSONObject(DATA);
        } catch (JSONException e) {
            return null;
        }
    }

    public static String convertWeiToTokenCurrency(String balance) {
        OstToken token = OstSdk.getToken(AppProvider.get().getCurrentUser().getTokenId());
        Integer decimals = Integer.parseInt(token.getBtDecimals());
        BigDecimal btWeiMultiplier = new BigDecimal(10).pow(decimals);
        BigDecimal bal = new BigDecimal(balance).divide(btWeiMultiplier);
        BigDecimal newBal = bal.setScale(5, RoundingMode.DOWN);
        return newBal.toString().replace(".00000", "");
    }

    public boolean handleActionEligibilityCheck(Context activityContext) {
        OstUser currentOstUser = AppProvider.get().getCurrentUser().getOstUser();
        if (currentOstUser.isActivating()) {
            Dialog dialog = DialogFactory.createSimpleOkErrorDialog(
                    activityContext,
                    activityContext.getResources().getString(R.string.wallet_being_setup),
                    activityContext.getResources().getString(R.string.wallet_setup_text));
            dialog.show();
            return true;
        }
        OstDevice currentDevice = currentOstUser.getCurrentDevice();
        if (currentDevice.isRecovering()) {
            Dialog dialog = DialogFactory.createSimpleOkErrorDialog(
                    activityContext,
                    "Your Wallet is recovering",
                    "The Wallet setup process takes about 12 hours. You can continue to use the app and we’ll notify when the wallet is ready to use.");
            dialog.show();
            return true;
        }
        return false;
    }

    public String formatWorkflowSuccessToast(OstWorkflowContext.WORKFLOW_TYPE workflowType, JSONObject workflowDetails){
        if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.SETUP_DEVICE)){
            return null;
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER)){
            return "User has been Activated!";
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION)){
            return "New Session has been Activated!";
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS)){
            return null;
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.EXECUTE_TRANSACTION)){
            if(workflowDetails != null){
                try{
                    String amount = "";
                    if(workflowDetails.getString("transferRule").equals(OstSdk.RULE_NAME_DIRECT_TRANSFER)){
                        amount = String.format("%s %s",
                                CommonUtils.convertWeiToTokenCurrency(workflowDetails.getString("amount")),
                                AppProvider.get().getCurrentEconomy().getTokenSymbol());
                    } else {
                        amount = String.format("%s USD", workflowDetails.getString("amount"));
                    }
                    return String.format("%s sent to %s successfully!",
                            amount,
                            workflowDetails.getString("userName"));
                } catch (Exception e){
                    return "Token Transfer is successful!";
                }
            } else {
                return "Token Transfer is successful!";
            }
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_QR_CODE)){
            return "New Device has been Authorized!";
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_MNEMONICS)){
            return "New Device has been Authorized!";
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.INITIATE_DEVICE_RECOVERY)){
            return "Recovery request has been Initiated. Device would be recovered in sometime!";
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.ABORT_DEVICE_RECOVERY)){
            return "Recovery request has been Aborted!";
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.REVOKE_DEVICE_WITH_QR_CODE)){
            return "Device has been Revoked Successfully!";
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN)){
            return "New PIN has been Activated!";
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.LOGOUT_ALL_SESSIONS)){
            return "All Sessions have been Logged Out!";
        }
        return null;
    }

    public String formatWorkflowFailedToast(OstWorkflowContext.WORKFLOW_TYPE workflowType, OstError ostError, JSONObject workflowDetails){
        if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.SETUP_DEVICE)){
            return null;
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER)){
            return ("User Activation Failed!\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION)){
            return ("Add Session Failed!\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.GET_DEVICE_MNEMONICS)){
            return ("Mnemonics cannot be fetched.\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.EXECUTE_TRANSACTION)){
            if(workflowDetails != null){
                try{
                    String amount = "";
                    if(workflowDetails.getString("transferRule").equals(OstSdk.RULE_NAME_DIRECT_TRANSFER)){
                        amount = String.format("%s %s",
                                CommonUtils.convertWeiToTokenCurrency(workflowDetails.getString("amount")),
                                AppProvider.get().getCurrentEconomy().getTokenSymbol());
                    } else {
                        amount = String.format("%s USD", workflowDetails.getString("amount"));
                    }
                    return String.format("Token Transfer of %s to %s failed!\n%s",
                            amount,
                            workflowDetails.getString("userName"), ostError.getMessage());
                } catch (Exception e){
                    return ("Transaction Failed!\n" + ostError.getMessage());
                }
            } else {
                return ("Transaction Failed!\n" + ostError.getMessage());
            }
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_QR_CODE)){
            return ("Device Authorization Failed!\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_MNEMONICS)){
            return ("Device Authorization Failed!\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.INITIATE_DEVICE_RECOVERY)){
            return ("Device Recovery Request Failed!\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.ABORT_DEVICE_RECOVERY)){
            return ("Recovery Request cannot be Aborted!\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.REVOKE_DEVICE_WITH_QR_CODE)){
            return ("Device Revoking Failed!\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.RESET_PIN)){
            return ("Reset PIN Failed!\n" + ostError.getMessage());
        } else if(workflowType.equals(OstWorkflowContext.WORKFLOW_TYPE.LOGOUT_ALL_SESSIONS)){
            return ("Sessions Logging Out Failed!\n" + ostError.getMessage());
        }
        return null;
    }

    public String getCurrentUserViewAddress(){
        String viewEndPoint = AppProvider.get().getCurrentEconomy().getViewApiEndpoint();
        try{
            LogInUser logInUser = AppProvider.get().getCurrentUser();
            JSONArray auxChains = OstSdk.getToken(logInUser.getTokenId()).getAuxiliaryChain();
            JSONObject jsonObject = auxChains.getJSONObject(0);
            String tokenAddr = jsonObject.getString("utility_branded_token");
            String url = viewEndPoint + "token/th-" + logInUser.getTokenId() + "-" +
                    tokenAddr + "-" +
                    logInUser.getOstUser().getTokenHolderAddress();
            return url;
        } catch (Exception e){ }
        return viewEndPoint;
    }
}