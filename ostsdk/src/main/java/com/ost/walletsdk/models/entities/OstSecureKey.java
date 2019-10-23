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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import com.ost.walletsdk.annotations.NonNull;

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