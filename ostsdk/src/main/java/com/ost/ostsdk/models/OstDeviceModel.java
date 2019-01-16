package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDevice;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstDeviceModel {

    void insertMultiSigWallet(OstDevice ostDevice, OstTaskCallback callback);

    void insertAllMultiSigWallets(OstDevice[] ostDevice, OstTaskCallback callback);

    void deleteMultiSigWallet(String id, OstTaskCallback callback);

    OstDevice[] getMultiSigWalletsByIds(String[] ids);

    OstDevice getMultiSigWalletById(String id);

    void deleteAllMultiSigWallets(OstTaskCallback callback);

    OstDevice initMultiSigWallet(JSONObject jsonObject, OstTaskCallback callback) throws JSONException;

    OstDevice[] getMultiSigWalletsByParentId(String id);
}