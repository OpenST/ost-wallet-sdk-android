/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.entity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ost.com.demoapp.AppProvider;


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

    public Transaction(String txnId, String txnHash, boolean in, String value, String metaName, String metaType, String metaDetails, int timestamp) {
        this.txnId = txnId;
        this.txnHash = txnHash;
        this.in = in;
        this.value = value;
        this.metaName = metaName;
        this.metaType = metaType;
        this.metaDetails = metaDetails;
        this.timestamp = timestamp;
    }

    public static List<Transaction> newInstance(JSONObject jsonObject) {
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
                if (from_userId.equals(currentUserId) || to_userId.equals(currentUserId)) {
                    boolean in = to_userId.equals(currentUserId);
                    String value = transfer.optString(AMOUNT);
                    list.add(new Transaction(txnId, txnHash, in ,value ,metaName, metaType, metaDetails, timestamp));
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
}
