package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "secure_key")
public class SecureKey {

    public static final String KEY = "key";
    public static final String DATA = "data";


    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "key")
    private String key = "";

    @NonNull
    @ColumnInfo(name = "parent_id")
    private String data = "";

    public SecureKey(@NonNull String key, @NonNull String data) {
        this.key = key;
        this.data = data;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public String getData() {
        return data;
    }
}
