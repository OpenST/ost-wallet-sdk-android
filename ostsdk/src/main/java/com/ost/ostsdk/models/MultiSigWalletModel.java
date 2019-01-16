package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDevice;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigWalletModel {

    void insertMultiSigWallet(OstDevice ostDevice, TaskCallback callback);

    void insertAllMultiSigWallets(OstDevice[] ostDevice, TaskCallback callback);

    void deleteMultiSigWallet(String id, TaskCallback callback);

    OstDevice[] getMultiSigWalletsByIds(String[] ids);

    OstDevice getMultiSigWalletById(String id);

    void deleteAllMultiSigWallets(TaskCallback callback);

    OstDevice initMultiSigWallet(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    OstDevice[] getMultiSigWalletsByParentId(String id);
}