package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstDeviceManagerModel {

    void insertMultiSig(OstDeviceManager ostDeviceManager);

    void insertAllMultiSigs(OstDeviceManager[] ostDeviceManager);

    void deleteMultiSig(String id);

    OstDeviceManager[] getMultiSigsByIds(String[] ids);

    OstDeviceManager getMultiSigById(String id);

    void deleteAllMultiSigs();

    OstDeviceManager initMultiSig(JSONObject jsonObject) throws JSONException;
}