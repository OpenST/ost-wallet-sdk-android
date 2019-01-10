package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.MultiSig;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigModel {

    void insertMultiSig(MultiSig multiSig, TaskCallback callback);

    void insertAllMultiSigs(MultiSig[] multiSig, TaskCallback callback);

    void deleteMultiSig(String id, TaskCallback callback);

    MultiSig[] getMultiSigsByIds(String[] ids);

    MultiSig getMultiSigById(String id);

    void deleteAllMultiSigs(TaskCallback callback);

    MultiSig initMultiSig(JSONObject jsonObject, TaskCallback callback) throws JSONException;
}