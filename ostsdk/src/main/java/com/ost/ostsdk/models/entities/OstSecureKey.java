package com.ost.ostsdk.models.entities;

import android.support.annotation.NonNull;

import org.web3j.utils.Numeric;

public class OstSecureKey {

    public static final String KEY = "key";
    public static final String DATA = "data";


    private String key = "";

    private String data;

    public OstSecureKey(@NonNull String key, @NonNull byte[] data) {
        this.key = key;
        this.data = Numeric.toHexString(data);
    }

    public OstSecureKey(@NonNull String key, @NonNull String data) {
        this.key = key;
        this.data = data;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public byte[] getData() {
        return Numeric.hexStringToByteArray(data);
    }

    @NonNull
    public String getStringData() {
        return data;
    }
}
