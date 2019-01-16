package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSecureKey;

public interface SecureKeyModel {

    void insertSecureKey(OstSecureKey ostSecureKey, TaskCallback callback);

    void insertAllSecureKeys(OstSecureKey[] ostSecureKey, TaskCallback callback);

    void deleteSecureKey(String id, TaskCallback callback);

    OstSecureKey[] getSecureKeysByIds(String[] ids);

    OstSecureKey getSecureKeyById(String id);

    void deleteAllSecureKeys(TaskCallback callback);

    OstSecureKey initSecureKey(String key, byte[] data);
}
