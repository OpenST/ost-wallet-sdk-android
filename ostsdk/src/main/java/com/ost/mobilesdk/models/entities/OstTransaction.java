package com.ost.mobilesdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.mobilesdk.models.Impls.OstModelFactory;

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

    public static String getIdentifier() {
        return OstTransaction.TRANSACTION_HASH;
    }

    public static class CONST_STATUS {
        public static final String CREATED = "created";
        public static final String QUEUED = "queued";
        public static final String SUBMITTED = "submitted";
        public static final String SUCCESS = "success";
        public static final String FAIL = "fail";
    }


    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstTransaction(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    public static OstTransaction parse(JSONObject jsonObject) throws JSONException {
        return (OstTransaction) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getTransactionModel(), getIdentifier(), getEntityFactory());
    }


    @Override
    protected OstTransaction updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstTransaction.parse(jsonObject);
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
        return this.getId();
    }

    public String getStatus() {
        return this.getJsonDataPropertyAsString(OstTransaction.STATUS);
    }

    public String getGasPrice() {
        return this.getJsonDataPropertyAsString(OstTransaction.GAS_PRICE);
    }

    public String getGasUsed() {
        return this.getJsonDataPropertyAsString(OstTransaction.GAS_USED);
    }

    public String getTransactionFee() {
        return this.getJsonDataPropertyAsString(OstTransaction.TRANSACTION_FEE);
    }

    public String getBlockTimestamp() {
        return this.getJsonDataPropertyAsString(OstTransaction.BLOCK_TIMESTAMP);
    }

    public String getBlockNumber() {
        return this.getJsonDataPropertyAsString(OstTransaction.BLOCK_NUMBER);
    }

    public String getRuleName() {
        return this.getJsonDataPropertyAsString(OstTransaction.RULE_NAME);
    }

    public String getTransfers() {
        //To-Do: Transfers is an array. Create Transfer Entity?
        return this.getJsonDataPropertyAsString(OstTransaction.TRANSFERS);
    }

    // Transactions can not have token-id or user-id as parent.
    // They represent actual block-chain transactions.
    // chain-id can be the only parent.
    public String getParentId() {
        return "";
    }

    /**
     * To-Do: Do If Time:
     * - Problem Statement:
     *   - SDK should provide an ability to show user ledger in offline mode.
     *
     * - Possible Solution:
     *   -  We need to create another entity OstTransactionContext
     *   - It shall have 2 columns:
     *   - user-id, transaction-hash
     *   - When ever we insert into Transactions, we also create a transaction context.
     *   - user-id shall be user-id of the device using which the api call was made.
     */

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }
}
