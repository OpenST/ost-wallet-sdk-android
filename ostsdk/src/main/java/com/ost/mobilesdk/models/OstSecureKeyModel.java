package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstSecureKey;

public interface OstSecureKeyModel {

    void insertSecureKey(OstSecureKey ostSecureKey, OstTaskCallback callback);

    OstSecureKey getByKey(String id);

    void deleteAllSecureKeys(OstTaskCallback callback);

    OstSecureKey initSecureKey(String key, byte[] data);
}