package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.MultiSigWallet;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigWalletModel {

    void insertMultiSigWallet(MultiSigWallet multiSigWallet, TaskCompleteCallback callback);

    void insertAllMultiSigWallets(MultiSigWallet[] multiSigWallet, TaskCompleteCallback callback);

    void deleteMultiSigWallet(MultiSigWallet multiSigWallet, TaskCompleteCallback callback);

    MultiSigWallet[] getMultiSigWalletsByIds(String[] ids);

    MultiSigWallet getMultiSigWalletById(String id);

    void deleteAllMultiSigWallets(TaskCompleteCallback callback);

    MultiSigWallet initMultiSigWallet(JSONObject jsonObject) throws JSONException;
}