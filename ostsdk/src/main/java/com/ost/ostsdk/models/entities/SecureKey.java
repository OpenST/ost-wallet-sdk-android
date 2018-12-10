package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;

@Entity(tableName = "secure_key")
public class SecureKey extends BaseEntity {

    public SecureKey() {
    }

    public String getKey() {
        return getId();
    }

    public String getValue() {
        return getData();
    }
}
