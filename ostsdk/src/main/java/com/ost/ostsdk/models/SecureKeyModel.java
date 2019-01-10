package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.SecureKey;

public interface SecureKeyModel {

    void insertSecureKey(SecureKey secureKey, TaskCallback callback);

    void insertAllSecureKeys(SecureKey[] secureKey, TaskCallback callback);

    void deleteSecureKey(String id, TaskCallback callback);

    SecureKey[] getSecureKeysByIds(String[] ids);

    SecureKey getSecureKeyById(String id);

    void deleteAllSecureKeys(TaskCallback callback);

    SecureKey initSecureKey(String key, byte[] data);
}
