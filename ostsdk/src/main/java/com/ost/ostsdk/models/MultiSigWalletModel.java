package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.MultiSigWallet;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigWalletModel {

    void insertMultiSigWallet(MultiSigWallet multiSigWallet, TaskCallback callback);

    void insertAllMultiSigWallets(MultiSigWallet[] multiSigWallet, TaskCallback callback);

    void deleteMultiSigWallet(String id, TaskCallback callback);

    MultiSigWallet[] getMultiSigWalletsByIds(String[] ids);

    MultiSigWallet getMultiSigWalletById(String id);

    void deleteAllMultiSigWallets(TaskCallback callback);

    MultiSigWallet initMultiSigWallet(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    MultiSigWallet[] getMultiSigWalletsByParentId(String id);
}