package com.ost.mobilesdk.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "bytes_storage")
//To-Do Rename OstSecureKey
public class OstSecureKey {

    public static final String KEY = "id";
    public static final String DATA = "data";


    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";

    @NonNull
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] data;

    public OstSecureKey(@NonNull String id, @NonNull byte[] data) {
        this.id = id;
        this.data = data;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public byte[] getData() {
        return data;
    }
}