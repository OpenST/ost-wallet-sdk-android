package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstSecureKey;
import com.ost.mobilesdk.utils.AsyncStatus;

import java.util.concurrent.Future;

public interface OstSecureKeyModel {

    Future<AsyncStatus> insertSecureKey(OstSecureKey ostSecureKey);

    OstSecureKey getByKey(String id);

    Future<AsyncStatus> deleteAllSecureKeys();

    OstSecureKey initSecureKey(String key, byte[] data);
}