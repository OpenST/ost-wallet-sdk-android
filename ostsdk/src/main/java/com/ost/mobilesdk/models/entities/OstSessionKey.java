package com.ost.mobilesdk.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.web3j.crypto.Keys;

@Entity(tableName = "session_key")
public class OstSessionKey {

    public static final String KEY = "key";
    public static final String DATA = "data";


    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "key")
    private String key = "";

    @NonNull
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] data;

    public OstSessionKey(@NonNull String key, @NonNull byte[] data) {
        this.key = Keys.toChecksumAddress(key);
        this.data = data;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public byte[] getData() {
        return data;
    }
}