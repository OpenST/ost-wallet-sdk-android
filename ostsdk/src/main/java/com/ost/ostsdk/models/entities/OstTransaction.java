package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "transaction")
public class OstTransaction extends OstBaseEntity {

    public static final String TRANSACTION_HASH = "transaction_hash";
    public static final String GAS_PRICE = "gas_price";
    public static final String GAS_USED = "gas_used";
    public static final String TRANSACTION_FEE = "transaction_fee";
    public static final String BLOCK_TIMESTAMP = "block_timestamp";
    public static final String BLOCK_NUMBER = "block_number";
    public static final String RULE_NAME = "rule_name";
    public static final String TRANSFERS = "transfers";

    public static class CONST_STATUS {
        public static final String CREATED = "CREATED";
        public static final String QUEUED = "QUEUED";
        public static final String SUBMITTED = "SUBMITTED";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAIL = "FAIL";
    }

    public static OstTransaction parse(JSONObject jsonObject) throws JSONException {
        OstTransaction ostTransaction = new OstTransaction(jsonObject);
        return OstModelFactory.getTransactionModel().insert(ostTransaction);
    }

    public OstTransaction(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstTransaction(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstTransaction.TRANSACTION_HASH) &&
                jsonObject.has(OstTransaction.GAS_PRICE) &&
                jsonObject.has(OstTransaction.GAS_USED) &&
                jsonObject.has(OstTransaction.TRANSACTION_FEE) &&
                jsonObject.has(OstTransaction.BLOCK_TIMESTAMP) &&
                jsonObject.has(OstTransaction.TRANSFERS) &&
                jsonObject.has(OstTransaction.RULE_NAME) &&
                jsonObject.has(OstTransaction.BLOCK_NUMBER);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);

    }

    public String getTransactionHash() {
        return getData().optString(OstTransaction.TRANSACTION_HASH,null);
    }

    public String getGasPrice() {
        return getData().optString(OstTransaction.GAS_PRICE,null);
    }

    public String getGasUsed() {
        return getData().optString(OstTransaction.GAS_USED,null);
    }

    public String getTransactionFee() {
        return getData().optString(OstTransaction.TRANSACTION_FEE,null);
    }

    public String getBlockTimestamp() {
        return getData().optString(OstTransaction.BLOCK_TIMESTAMP,null);
    }

    public String getBlockNumber() {
        return getData().optString(OstTransaction.BLOCK_NUMBER,null);
    }

    public String getRuleName() {
        return getData().optString(OstTransaction.RULE_NAME,null);
    }

    public String getTransfers() {
        return getData().optString(OstTransaction.TRANSFERS,null);
    }

    @Override
    String getEntityIdKey() {
        return OstTransaction.TRANSACTION_HASH;
    }
}
