package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.MultiSig;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigModel {

    void insertMultiSig(MultiSig multiSig, TaskCompleteCallback callback);

    void insertAllMultiSigs(MultiSig[] multiSig, TaskCompleteCallback callback);

    void deleteMultiSig(MultiSig multiSig, TaskCompleteCallback callback);

    MultiSig[] getMultiSigsByIds(String[] ids);

    MultiSig getMultiSigById(String id);

    void deleteAllMultiSigs(TaskCompleteCallback callback);

    MultiSig initMultiSig(JSONObject jsonObject) throws JSONException;
}