package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstSessionKey;

public interface OstSessionKeyModel {

    void insertSessionKey(OstSessionKey ostSessionKey, OstTaskCallback ostTaskCallback);

    OstSessionKey getByKey(String key);

    void deleteAllSessionKeys();

    OstSessionKey initSessionKey(String key, byte[] data);
}