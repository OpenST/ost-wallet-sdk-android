package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.content.Context;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.security.impls.AndroidSecureStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

/**
 * Transaction Signing
 */
@Entity(tableName = "multi_sig_wallet")
public class MultiSigWallet extends BaseEntity {

    public static final String STATUS = "status";
    public static final String ADDRESS = "address";
    public static final String MULTI_SIG_ID = "multi_sig_id";

    @Ignore
    private String status;
    @Ignore
    private String multiSigId;
    @Ignore
    private String address;

    public MultiSigWallet(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private MultiSigWallet(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public MultiSigWallet() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(MultiSigWallet.ADDRESS) &&
                jsonObject.has(MultiSigWallet.STATUS) &&
                jsonObject.has(MultiSigWallet.MULTI_SIG_ID);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setAddress(jsonObject.getString(MultiSigWallet.ADDRESS));
        setStatus(jsonObject.getString(MultiSigWallet.STATUS));
        setMultiSigId(jsonObject.getString(MultiSigWallet.MULTI_SIG_ID));
    }

    public String signTransaction(RawTransaction rawTransaction) {
        Context context = null;
        String alias = "alias";
        String data = OstSdkKeyDatabase.getDatabase().secureKeyDao().getById(alias).getData();
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(AndroidSecureStorage.getInstance(OstSdk.getContext(), alias).decrypt(data.getBytes()).toString()));
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
}