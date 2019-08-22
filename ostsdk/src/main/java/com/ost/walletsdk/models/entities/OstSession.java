/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.text.TextUtils;
import android.util.Log;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.impls.OstAndroidSecureStorage;
import com.ost.walletsdk.models.Impls.OstModelFactory;
import com.ost.walletsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.walletsdk.models.OstSessionModel;
import com.ost.walletsdk.utils.EIP1077;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * To hold Session info
 */
@Entity(tableName = "session")
public class OstSession extends OstBaseEntity {
    private static final String TAG = "OstSession";

    public static final String STATUS = "status";
    public static final String ADDRESS = "address";
    public static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    public static final String USER_ID = "user_id";
    public static final String EXPIRATION_HEIGHT = "expiration_height";
    public static final String APPROX_EXPIRATION_TIMESTAMP = "approx_expiration_timestamp";
    public static final String SPENDING_LIMIT = "spending_limit";
    public static final String NONCE = "nonce";

    public static String getIdentifier() {
        return OstSession.ADDRESS;
    }

    public static OstSession getById(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        id = Keys.toChecksumAddress(id);
        return OstModelFactory.getSessionModel().getEntityById(id);
    }

    public static boolean isValidStatus(String status) {
        return Arrays.asList(CONST_STATUS.CREATED,
                CONST_STATUS.INITIALIZING,
                CONST_STATUS.AUTHORISED,
                CONST_STATUS.EXPIRED,
                CONST_STATUS.REVOKED,
                CONST_STATUS.REVOKING)
                .contains(status);
    }

    public static List<OstSession> getSessionsToSync(String parentId) {
        return getSessions(parentId,
                CONST_STATUS.AUTHORISED,
                CONST_STATUS.INITIALIZING,
                CONST_STATUS.CREATED);
    }

    public static List<OstSession> getActiveSessions(String parentId) {
        return getSessions(parentId, CONST_STATUS.AUTHORISED);
    }
    private static List<OstSession> getSessions(String parentId, String ...statuses) {
        OstSession[] ostSessions = getSessionsByParentId(parentId);
        ArrayList<OstSession> activeSessionList = new ArrayList<>();
        for (OstSession ostSession: ostSessions) {
            for (String status : statuses) {
                if (status.equalsIgnoreCase(ostSession.getStatus())) {
                    activeSessionList.add(ostSession);
                    break;
                }
            }

        }
        Collections.sort(activeSessionList, new Comparator<OstSession>() {
            @Override
            public int compare(OstSession o1, OstSession o2) {
                Double sessionATimestamp = o1.getUpdatedTimestamp();
                sessionATimestamp = Math.abs( sessionATimestamp );

                Double sessionBTimestamp = o2.getUpdatedTimestamp();
                sessionBTimestamp = Math.abs( sessionBTimestamp );

                // Sort in increasing order of timestamp.
                Double diff = (sessionATimestamp - sessionBTimestamp);
                return diff.intValue();
            }
        });
        return activeSessionList;
    }

    public static OstSession[] getSessionsByParentId(String parentId) {
        OstSessionModel ostSessionModel = OstModelFactory.getSessionModel();
        return ostSessionModel.getEntitiesByParentId(parentId);
    }

    public static class CONST_STATUS {
        public static final String CREATED = "created";
        public static final String INITIALIZING = "initializing";
        public static final String AUTHORISED = "authorized";
        public static final String EXPIRED = "expired";
        public static final String REVOKING = "revoking";
        public static final String REVOKED = "revoked";
    }

    private static EntityFactory entityFactory;
    private static EntityFactory getEntityFactory() {
        if ( null == entityFactory ) {
            entityFactory = new EntityFactory() {
                @Override
                public OstBaseEntity createEntity(JSONObject jsonObject) throws JSONException {
                    return new OstSession(jsonObject);
                }
            };
        }
        return entityFactory;
    }

    /**
     * Create Session entity locally with initializing status
     *
     * @param address session address
     * @param userId  user id
     * @return OstSession object
     */
    public static OstSession init(String address, String userId) {
        OstSession ostSession = OstSession.getById(address);
        if (null != ostSession) {
            Log.i(TAG, String.format("OstSession with address %s already exist", address));
            return ostSession;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OstSession.ADDRESS, address);
            jsonObject.put(OstSession.USER_ID, userId);
            jsonObject.put(OstSession.SPENDING_LIMIT, "0");
            jsonObject.put(OstSession.EXPIRATION_HEIGHT, "0");
            jsonObject.put(OstSession.APPROX_EXPIRATION_TIMESTAMP, "0");
            jsonObject.put(OstSession.NONCE, "0");
            jsonObject.put(OstSession.UPDATED_TIMESTAMP, System.currentTimeMillis());
            jsonObject.put(OstSession.STATUS, CONST_STATUS.CREATED);
            return OstSession.parse(jsonObject);
        } catch (JSONException e) {
            Log.i(TAG, "Unexpected JSON exception" , e);
        }

        return null;
    }

    public static OstSession parse(JSONObject jsonObject) throws JSONException {
        return (OstSession) OstBaseEntity.insertOrUpdate( jsonObject, OstModelFactory.getSessionModel(), getIdentifier(), getEntityFactory());
    }

    @Override
    protected OstSession updateWithJsonObject(JSONObject jsonObject) throws JSONException {
        return OstSession.parse(jsonObject);
    }


    public OstSession(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstSession(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstSession.ADDRESS) &&
                jsonObject.has(OstSession.USER_ID) &&
                jsonObject.has(OstSession.SPENDING_LIMIT) &&
                jsonObject.has(OstSession.EXPIRATION_HEIGHT) &&
                jsonObject.has(OstSession.APPROX_EXPIRATION_TIMESTAMP) &&
                jsonObject.has(OstSession.NONCE);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

//    public String signTransaction(JSONObject jsonObject, String userId) throws Exception {
//        byte[] data = new OstSecureKeyModelRepository().getByKey(getAddress()).getData();
//        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(new EIP1077(jsonObject).toEIP1077TransactionHash()), ECKeyPair.create(OstAndroidSecureStorage.getInstance(OstSdk.getContext(), userId).decrypt(data)));
//        String signedMessage = Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + Integer.toHexString(signatureData.getV() & 0xFF);
//        return signedMessage;
//    }
//
//    public String signTransaction(OstSession.Transaction transaction, String userId) throws Exception {
//        return signTransaction(transaction.toJSONObject(), userId);
//    }

    @Override
    public String getId() {
        String id = super.getId();
        id = Keys.toChecksumAddress(id);
        return id;
    }

    public String getAddress() {
        return this.getId();
    }


    public String getStatus() {
        return this.getJsonDataPropertyAsString(OstSession.STATUS);
    }


    /**
     * @deprecated TokenHolderAddress is not available in Session. Please use user.getTokenHolderAddress method instead.
     * @return Will always return null.
     */
    public String getTokenHolderAddress() {
        String tokenHolderAddress = this.getJsonDataPropertyAsString(OstSession.TOKEN_HOLDER_ADDRESS);
        if (null != tokenHolderAddress) {
            tokenHolderAddress = Keys.toChecksumAddress(tokenHolderAddress);
        }
        return tokenHolderAddress;
    }


    public String getExpirationHeight() {
        return this.getJsonDataPropertyAsString(OstSession.EXPIRATION_HEIGHT);
    }

    public String getExpirationTimestamp() {
        return this.getJsonDataPropertyAsString(OstSession.APPROX_EXPIRATION_TIMESTAMP);
    }

    public String getSpendingLimit() {
        return this.getJsonDataPropertyAsString(OstSession.SPENDING_LIMIT);
    }

    public int getNonce() {
        JSONObject jsonObject = this.getJSONData();
        if (null == jsonObject) {
            Log.e(TAG, "getRequirement: jsonObject is null");
            return -1;
        }
        return jsonObject.optInt(OstDeviceManager.NONCE, -1);
    }

    public int incrementNonce() {
        int currentNonce = getNonce();
        int newNonce = currentNonce + 1;
        try {
            setJsonDataProperty(NONCE, newNonce);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected exception", e);
            return currentNonce;
        }
        return newNonce;
    }

    @Override
    String getEntityIdKey() {
        return getIdentifier();
    }

    @Override
    public String getParentIdKey() {
        return OstSession.USER_ID;
    }

    public static class Transaction {
        private static final String TAG = "THS.Transaction";
        private BigInteger value = new BigInteger("0");
        private BigInteger gas = new BigInteger("0");
        private String fromAddress = "0x0";

        public Transaction setToAddress(String toAddress) {
            this.toAddress = toAddress;
            return this;
        }

        public Transaction setGasPrice(BigInteger gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public Transaction setGasToken(BigInteger gasToken) {
            this.gasToken = gasToken;
            return this;
        }

        public Transaction setTxnOperationType(String txnOperationType) {
            this.txnOperationType = txnOperationType;
            return this;
        }

        public Transaction setNonce(BigInteger nonce) {
            this.nonce = nonce;
            return this;
        }

        public Transaction setData(String data) {
            this.data = data;
            return this;
        }

        public Transaction setTxnExtraHash(String txnExtraHash) {
            this.txnExtraHash = txnExtraHash;
            return this;
        }

        public Transaction setTxnCallPrefix(String txnCallPrefix) {
            this.txnCallPrefix = txnCallPrefix;
            return this;
        }

        public Transaction setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        Transaction setValue(BigInteger bigInteger) {
            this.value = bigInteger;
            return this;
        }

        Transaction setGas(BigInteger bigInteger) {
            this.gas = bigInteger;
            return this;
        }

        private String toAddress = "0x0";
        private BigInteger gasPrice = new BigInteger("0");
        private BigInteger gasToken = new BigInteger("0");
        private String txnOperationType = "0";
        private BigInteger nonce = new BigInteger("0");
        private String data = "0x0";
        private String txnExtraHash = "0x0";
        private String txnCallPrefix = "0x0";

        Transaction(String fromAddress, String toAddress) {
            this.fromAddress = fromAddress;
            this.toAddress = toAddress;
        }

        JSONObject toJSONObject() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(EIP1077.TXN_GAS, this.gas.toString());
                jsonObject.put(EIP1077.TXN_VALUE, this.value.toString());
                jsonObject.put(EIP1077.TXN_FROM, this.fromAddress);
                jsonObject.put(EIP1077.TXN_TO, this.toAddress);
                jsonObject.put(EIP1077.TXN_GAS_PRICE, this.gasPrice);
                jsonObject.put(EIP1077.TXN_GAS_TOKEN, this.gasToken);
                jsonObject.put(EIP1077.TXN_OPERATION_TYPE, this.txnOperationType);
                jsonObject.put(EIP1077.TXN_NONCE, this.nonce);
                jsonObject.put(EIP1077.TXN_DATA, this.data);
                jsonObject.put(EIP1077.TXN_EXTRA_HASH, this.txnExtraHash);
                jsonObject.put(EIP1077.TXN_CALL_PREFIX, this.txnCallPrefix);
                return jsonObject;
            } catch (JSONException jsonException) {
                Log.e(TAG, "JSON exception");
                return null;
            }

        }
    }
}
