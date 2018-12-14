package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.SecureKey;

import org.json.JSONObject;

public interface SecureKeyModel {

    void insertSecureKey(SecureKey secureKey, TaskCallback callback);

    void insertAllSecureKeys(SecureKey[] secureKey, TaskCallback callback);

    void deleteSecureKey(SecureKey secureKey, TaskCallback callback);

    SecureKey[] getSecureKeysByIds(String[] ids);

    SecureKey getSecureKeyById(String id);

    void deleteAllSecureKeys(TaskCallback callback);

    SecureKey initSecureKey(JSONObject jsonObject);
}
