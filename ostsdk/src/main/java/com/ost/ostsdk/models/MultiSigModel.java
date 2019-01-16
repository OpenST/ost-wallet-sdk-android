package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigModel {

    void insertMultiSig(OstDeviceManager ostDeviceManager, TaskCallback callback);

    void insertAllMultiSigs(OstDeviceManager[] ostDeviceManager, TaskCallback callback);

    void deleteMultiSig(String id, TaskCallback callback);

    OstDeviceManager[] getMultiSigsByIds(String[] ids);

    OstDeviceManager getMultiSigById(String id);

    void deleteAllMultiSigs(TaskCallback callback);

    OstDeviceManager initMultiSig(JSONObject jsonObject, TaskCallback callback) throws JSONException;
}