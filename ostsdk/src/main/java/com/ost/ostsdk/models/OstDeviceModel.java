package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDevice;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstDeviceModel {

    void insertMultiSigWallet(OstDevice ostDevice);

    void insertAllMultiSigWallets(OstDevice[] ostDevice);

    void deleteMultiSigWallet(String id);

    OstDevice[] getMultiSigWalletsByIds(String[] ids);

    OstDevice getMultiSigWalletById(String id);

    void deleteAllMultiSigWallets();

    OstDevice initMultiSigWallet(JSONObject jsonObject) throws JSONException;

    OstDevice[] getMultiSigWalletsByParentId(String id);
}