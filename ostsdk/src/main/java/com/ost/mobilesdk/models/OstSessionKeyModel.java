package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstSessionKey;
import com.ost.mobilesdk.utils.AsyncStatus;

import java.util.concurrent.Future;

public interface OstSessionKeyModel {

    Future<AsyncStatus> insertSessionKey(OstSessionKey ostSessionKey);

    OstSessionKey getByKey(String key);

    Future<AsyncStatus> deleteAllSessionKeys();

    OstSessionKey initSessionKey(String key, byte[] data);
}