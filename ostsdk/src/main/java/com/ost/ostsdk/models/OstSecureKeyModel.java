package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSecureKey;

public interface OstSecureKeyModel {

    void insertSecureKey(OstSecureKey ostSecureKey, OstTaskCallback callback);

    OstSecureKey getByKey(String id);

    void deleteAllSecureKeys(OstTaskCallback callback);

    OstSecureKey initSecureKey(String key, byte[] data);
}
