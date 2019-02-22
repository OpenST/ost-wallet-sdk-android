package com.ost.mobilesdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstModelFactory;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.OstSessionModel;
import com.ost.mobilesdk.security.impls.OstAndroidSecureStorage;
import com.ost.mobilesdk.utils.EIP1077;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EIP1077 Transaction Signing
 */
@Entity(tableName = "session")
public class OstSession extends OstBaseEntity {

    public static final String STATUS = "status";
    public static final String ADDRESS = "address";
    public static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    public static final String USER_ID = "user_id";
    public static final String EXPIRATION_BLOCK_HEIGHT = "expiration_block_height";
    public static final String SPENDING_LIMIT = "spending_limit";
    public static final String NONCE = "nonce";

    public static String getIdentifier() {
        return OstSession.ADDRESS;
    }

    public static OstSession getById(String entityId) {
        return OstModelFactory.getSessionModel().getEntityById(entityId);
    }

    public static boolean isValidStatus(String status) {
        return Arrays.asList(CONST_STATUS.CREATED, CONST_STATUS.INITIALIZING, CONST_STATUS.AUTHORISED,CONST_STATUS.EXPIRED,
                CONST_STATUS.REVOKED, CONST_STATUS.REVOKING).contains(status);
    }

    public static List<OstSession> getSessionsToSync(String parentId) {
        return getSessions(parentId, CONST_STATUS.AUTHORISED, CONST_STATUS.INITIALIZING);
    }

    public static List<OstSession> getActiveSessions(String parentId) {
        return getSessions(parentId, CONST_STATUS.AUTHORISED);
    }
    private static List<OstSession> getSessions(String parentId, String ...statuses) {
        OstSession[] ostSessions = getSessionsByParentId(parentId);
        List<OstSession> activeSessionList = new ArrayList<>();
        for (OstSession ostSession: ostSessions) {
            for (String status : statuses) {
                if (status.equalsIgnoreCase(ostSession.getStatus())) {
                    activeSessionList.add(ostSession);
                    break;
                }
            }

        }
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
                jsonObject.has(OstSession.NONCE);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String signTransaction(JSONObject jsonObject, String userId) throws Exception {
        byte[] data = new OstSecureKeyModelRepository().getByKey(getAddress()).getData();
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(new EIP1077(jsonObject).toEIP1077TransactionHash()), ECKeyPair.create(OstAndroidSecureStorage.getInstance(OstSdk.getContext(), userId).decrypt(data)));
        String signedMessage = Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + Integer.toHexString(signatureData.getV() & 0xFF);
        return signedMessage;
    }

    public String signTransaction(OstSession.Transaction transaction, String userId) throws Exception {
        return signTransaction(transaction.toJSONObject(), userId);
    }

    public String getAddress() {
        return this.getId();
    }


    public String getStatus() {
        return this.getJsonDataPropertyAsString(OstSession.STATUS);
    }


    public String getTokenHolderAddress() {
        return this.getJsonDataPropertyAsString(OstSession.TOKEN_HOLDER_ADDRESS);
    }


    public String getExpirationBlockHeight() {
        return this.getJsonDataPropertyAsString(OstSession.EXPIRATION_BLOCK_HEIGHT);
    }

    public String getSpendingLimit() {
        return this.getJsonDataPropertyAsString(OstSession.SPENDING_LIMIT);
    }

    public String getNonce() {
        return this.getJsonDataPropertyAsString(OstSession.NONCE);
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
