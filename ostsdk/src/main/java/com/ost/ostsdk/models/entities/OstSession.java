package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.util.Log;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.security.impls.OstAndroidSecureStorage;
import com.ost.ostsdk.utils.EIP1077;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

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

    public static OstSession parse(JSONObject jsonObject) throws JSONException {
        OstSession ostSession = new OstSession(jsonObject);
        return OstModelFactory.getSessionModel().insert(ostSession);
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
                jsonObject.has(OstSession.STATUS) &&
                jsonObject.has(OstSession.ADDRESS) &&
                jsonObject.has(OstSession.TOKEN_HOLDER_ADDRESS) &&
                jsonObject.has(OstSession.EXPIRATION_BLOCK_HEIGHT) &&
                jsonObject.has(OstSession.SPENDING_LIMIT) &&
                jsonObject.has(OstSession.USER_ID) &&
                jsonObject.has(OstSession.NONCE);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String signTransaction(JSONObject jsonObject, String userId) throws Exception {
        byte[] data = new OstSecureKeyModelRepository().getById(getAddress()).getData();
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(new EIP1077(jsonObject).toEIP1077TransactionHash()), ECKeyPair.create(OstAndroidSecureStorage.getInstance(OstSdk.getContext(), userId).decrypt(data)));
        String signedMessage = Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + Integer.toHexString(signatureData.getV() & 0xFF);
        return signedMessage;
    }

    public String signTransaction(OstSession.Transaction transaction, String userId) throws Exception {
        return signTransaction(transaction.toJSONObject(), userId);
    }

    public String getAddress() {
        return getData().optString(OstSession.ADDRESS, null);
    }


    public String getStatus() {
        return getData().optString(OstSession.STATUS, null);
    }


    public String getTokenHolderAddress() {
        return getData().optString(OstSession.TOKEN_HOLDER_ADDRESS, null);
    }


    public String getExpirationBlockHeight() {
        return getData().optString(OstSession.EXPIRATION_BLOCK_HEIGHT, null);
    }


    public String getSpendingLimit() {
        return getData().optString(OstSession.SPENDING_LIMIT, null);
    }

    public String getNonce() {
        return getData().optString(OstSession.NONCE, null);
    }

    @Override
    String getEntityIdKey() {
        return OstUser.ID;
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
