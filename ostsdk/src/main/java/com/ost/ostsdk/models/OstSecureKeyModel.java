package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSecureKey;

public interface OstSecureKeyModel {

    void insertSecureKey(OstSecureKey ostSecureKey, OstTaskCallback callback);

    void insertAllSecureKeys(OstSecureKey[] ostSecureKey, OstTaskCallback callback);

    void deleteSecureKey(String id, OstTaskCallback callback);

    OstSecureKey[] getSecureKeysByIds(String[] ids);

    OstSecureKey getSecureKeyById(String id);

    void deleteAllSecureKeys(OstTaskCallback callback);

    OstSecureKey initSecureKey(String key, byte[] data);
}
