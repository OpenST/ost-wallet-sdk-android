package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSessionKey;

public interface OstSessionKeyModel {

    void insertSessionKey(OstSessionKey ostSessionKey);

    OstSessionKey getByKey(String key);

    void deleteAllSessionKeys();

    OstSessionKey initSessionKey(String key, byte[] data);
}