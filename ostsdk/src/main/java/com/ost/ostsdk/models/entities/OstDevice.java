package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.SecureKeyModelRepository;
import com.ost.ostsdk.security.impls.AndroidSecureStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

/**
 * Transaction Signing
 */
@Entity(tableName = "device")
public class OstDevice extends OstBaseEntity {

    public static final String STATUS = "status";
    public static final String ADDRESS = "address";
    public static final String MULTI_SIG_ID = "multi_sig_id";


    public static final String CREATED_STATUS = "CREATED";
    public static final String DELETED_STATUS = "DELETED";

    @Ignore
    private String status;
    @Ignore
    private String multiSigId;
    @Ignore
    private String address;

    public OstDevice(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private OstDevice(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public OstDevice() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstDevice.ADDRESS) &&
                jsonObject.has(OstDevice.STATUS) &&
                jsonObject.has(OstDevice.MULTI_SIG_ID);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setAddress(jsonObject.getString(OstDevice.ADDRESS));
        setStatus(jsonObject.getString(OstDevice.STATUS));
        setMultiSigId(jsonObject.getString(OstDevice.MULTI_SIG_ID));
    }

    public String signTransaction(RawTransaction rawTransaction, String userId) {
        byte[] data = new SecureKeyModelRepository().getById(getAddress()).getData();
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(Numeric.toHexString(AndroidSecureStorage.getInstance(OstSdk.getContext(), userId).decrypt(data))));
        return Numeric.toHexString(signedMessage);
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

    public String getMultiSigId() {
        return multiSigId;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    private void setMultiSigId(String multiSigId) {
        this.multiSigId = multiSigId;
    }

    public static class Transaction extends RawTransaction {

        public Transaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data) {
            super(nonce, gasPrice, gasLimit, to, value, data);
        }
    }
}