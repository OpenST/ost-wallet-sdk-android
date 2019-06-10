/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.entity;

import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.ost.ostwallet.AppProvider;


public class Transaction {
    private static final String LOG_TAG = "TransactionEntity";

    private static final String TXN_HASH = "transaction_hash";
    private static final String TXN_ID = "id";
    private static final String META_PROPERTY = "meta_property";
    private static final String META_NAME = "name";
    private static final String META_TYPE = "type";
    private static final String META_DETAILS = "details";
    private static final String TRANSFERS = "transfers";
    private static final String FROM_USER_ID = "from_user_id";
    private static final String TO_USER_ID = "to_user_id";
    private static final String AMOUNT = "amount";
    private static final String BLOCK_TIMESTAMP = "block_timestamp";
    private int timestamp;

    public String getTxnId() {
        return txnId;
    }

    public String getTxnHash() {
        return txnHash;
    }

    public boolean isIn() {
        return in;
    }

    public String getValue() {
        return value;
    }

    public String getMetaName() {
        return metaName;
    }

    public String getMetaType() {
        return metaType;
    }

    public String getMetaDetails() {
        return metaDetails;
    }

    private final String txnId;
    private final String txnHash;
    private final boolean in;
    private final String value;
    private final String metaName;
    private final String metaType;
    private final String metaDetails;
    private String fromUserName;
    private String toUserName;

    public Transaction(String txnId, String txnHash, boolean in, String value, String metaName, String metaType,
                       String metaDetails, int timestamp, String fromUserName, String toUserName) {
        this.txnId = txnId;
        this.txnHash = txnHash;
        this.in = in;
        this.value = value;
        this.metaName = metaName;
        this.metaType = metaType;
        this.metaDetails = metaDetails;
        this.timestamp = timestamp;
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
    }

    public static List<Transaction> newInstance(JSONObject jsonObject, JSONObject transactionUsers) {
        List<Transaction> list = new ArrayList<>();
        try {
            String txnId = jsonObject.getString(TXN_ID);
            String txnHash = jsonObject.getString(TXN_HASH);
            int timestamp = jsonObject.getInt(BLOCK_TIMESTAMP);
            JSONObject metaProperty = jsonObject.getJSONObject(META_PROPERTY);
            String metaName = metaProperty.optString(META_NAME);
            String metaType = metaProperty.optString(META_TYPE);
            String metaDetails = metaProperty.optString(META_DETAILS);

            JSONArray transfersJSONArray = jsonObject.optJSONArray(TRANSFERS);

            String currentUserId = AppProvider.get().getCurrentUser().getOstUserId();
            for (int i=0; i <transfersJSONArray.length(); i++) {
                JSONObject transfer = transfersJSONArray.getJSONObject(i);
                String from_userId = transfer.optString(FROM_USER_ID);
                String to_userId = transfer.optString(TO_USER_ID);
                String fromUserName = Transaction.getNameFromUid(from_userId, transfer.optString("from"), transactionUsers);
                String toUserName = Transaction.getNameFromUid(to_userId, transfer.optString("to"), transactionUsers);
                if (from_userId.equals(currentUserId) || to_userId.equals(currentUserId)) {
                    boolean in = to_userId.equals(currentUserId);
                    String value = transfer.optString(AMOUNT);
                    list.add(new Transaction(txnId, txnHash, in ,value ,metaName, metaType, metaDetails,
                            timestamp, fromUserName, toUserName));
                }

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON exception", e.getCause());
        }
        return list;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    private static String getNameFromUid(String userId, String address, JSONObject transactionUsers){
        String name = null;
        if(null != transactionUsers){
            JSONObject userObj = transactionUsers.optJSONObject(userId);
            name = (null != userObj) ? userObj.optString("username") : null;
        }
        if(null == name && null != address){
            try{
                OstToken ostToken = OstSdk.getToken(AppProvider.get().getCurrentUser().getTokenId());
                if(null != ostToken.getCompanyTokenHolders()){
                    for(int i=0;i<ostToken.getCompanyTokenHolders().length();i++){
                        String cta = ostToken.getCompanyTokenHolders().getString(i);
                        if(cta.equalsIgnoreCase(address)){
                            name = ostToken.getName();
                            break;
                        }
                    }
                }
            } catch (Exception e){}
        }
        return name;
    }
}
