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