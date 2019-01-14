package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.util.Log;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.SecureKeyModelRepository;
import com.ost.ostsdk.security.impls.AndroidSecureStorage;
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
@Entity(tableName = "token_holder_session")
public class TokenHolderSession extends BaseEntity {

    public static final String STATUS = "status";
    public static final String ADDRESS = "address";
    public static final String TOKEN_HOLDER_ID = "token_holder_id";
    public static final String BLOCK_HEIGHT = "block_height";
    public static final String EXPIRY_TIME = "expiry_time";
    public static final String SPENDING_LIMIT = "spending_limit";
    public static final String REDEMPTION_LIMIT = "redemption_limit";
    public static final String NONCE = "nonce";

    @Ignore
    private String status;
    @Ignore
    private String address;
    @Ignore
    private String tokenHolderId;
    @Ignore
    private String blockHeight;
    @Ignore
    private String expiryTime;
    @Ignore
    private String spendingLimit;
    @Ignore
    private String redemptionLimit;
    @Ignore
    private String nonce;


    public TokenHolderSession(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private TokenHolderSession(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public TokenHolderSession() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(TokenHolderSession.STATUS) &&
                jsonObject.has(TokenHolderSession.ADDRESS) &&
                jsonObject.has(TokenHolderSession.TOKEN_HOLDER_ID) &&
                jsonObject.has(TokenHolderSession.BLOCK_HEIGHT) &&
                jsonObject.has(TokenHolderSession.EXPIRY_TIME) &&
                jsonObject.has(TokenHolderSession.REDEMPTION_LIMIT) &&
                jsonObject.has(TokenHolderSession.SPENDING_LIMIT) &&
                jsonObject.has(TokenHolderSession.NONCE);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setStatus(jsonObject.getString(TokenHolderSession.STATUS));
        setAddress(jsonObject.getString(TokenHolderSession.ADDRESS));
        setTokenHolderId(jsonObject.getString(TokenHolderSession.TOKEN_HOLDER_ID));
        setBlockHeight(jsonObject.getString(TokenHolderSession.BLOCK_HEIGHT));
        setExpiryTime(jsonObject.getString(TokenHolderSession.EXPIRY_TIME));
        setRedemptionLimit(jsonObject.getString(TokenHolderSession.REDEMPTION_LIMIT));
        setSpendingLimit(jsonObject.getString(TokenHolderSession.SPENDING_LIMIT));
        setNonce(jsonObject.getString(TokenHolderSession.NONCE));
    }

    public String signTransaction(JSONObject jsonObject, String userId) throws Exception {
        byte[] data = new SecureKeyModelRepository().getById(getAddress()).getData();
        byte[] signedMessage = Sign.signMessage(Numeric.hexStringToByteArray(new EIP1077(jsonObject).toEIP1077TransactionHash()), ECKeyPair.create(AndroidSecureStorage.getInstance(OstSdk.getContext(), userId).decrypt(data))).getR();
        return Numeric.toHexString(signedMessage);
    }

    public String signTransaction(TokenHolderSession.Transaction transaction, String userId) throws Exception {
        return signTransaction(transaction.toJSONObject(), userId);
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    public String getTokenHolderId() {
        return tokenHolderId;
    }

    private void setTokenHolderId(String tokenHolderId) {
        this.tokenHolderId = tokenHolderId;
    }

    public String getBlockHeight() {
        return blockHeight;
    }

    private void setBlockHeight(String blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    private void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getSpendingLimit() {
        return spendingLimit;
    }

    private void setSpendingLimit(String spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public String getRedemptionLimit() {
        return redemptionLimit;
    }

    private void setRedemptionLimit(String redemptionLimit) {
        this.redemptionLimit = redemptionLimit;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public static class Transaction {
        private static final String TAG = "THS.Transaction";
        private BigInteger value = new BigInteger("0");
        private BigInteger gas = new BigInteger("0");

        Transaction() {

        }

        Transaction setValue(BigInteger bigInteger) {
            this.value = bigInteger;
            return this;
        }

        Transaction setGas(BigInteger bigInteger) {
            this.gas = bigInteger;
            return this;
        }

        JSONObject toJSONObject() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(EIP1077.TXN_GAS, this.gas.toString());
                return jsonObject;
            } catch (JSONException jsonException) {
                Log.e(TAG, "JSON exception");
                return null;
            }

        }
    }
}
