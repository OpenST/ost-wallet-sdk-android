package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstDeviceManagerModel {

    void insertMultiSig(OstDeviceManager ostDeviceManager, OstTaskCallback callback);

    void insertAllMultiSigs(OstDeviceManager[] ostDeviceManager, OstTaskCallback callback);

    void deleteMultiSig(String id, OstTaskCallback callback);

    OstDeviceManager[] getMultiSigsByIds(String[] ids);

    OstDeviceManager getMultiSigById(String id);

    void deleteAllMultiSigs(OstTaskCallback callback);

    OstDeviceManager initMultiSig(JSONObject jsonObject, OstTaskCallback callback) throws JSONException;
}