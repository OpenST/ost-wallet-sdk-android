package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.SecureKey;

import org.json.JSONObject;

public interface SecureKeyModel {

    void insertSecureKey(SecureKey secureKey, TaskCompleteCallback callback);

    void insertAllSecureKeys(SecureKey[] secureKey, TaskCompleteCallback callback);

    void deleteSecureKey(SecureKey secureKey, TaskCompleteCallback callback);

    SecureKey[] getSecureKeysByIds(String[] ids);

    SecureKey getSecureKeyById(String id);

    void deleteAllSecureKeys(TaskCompleteCallback callback);

    SecureKey initSecureKey(JSONObject jsonObject);
}
