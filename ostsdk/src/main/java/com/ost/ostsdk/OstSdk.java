package com.ost.ostsdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.Economy;
import com.ost.ostsdk.models.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Work Flows
 * 1. Key Recovery (Work flow, key Rotation)
 * 2. Additional Key Provisioning (Work Flow)
 * 3. QR code scanning (Work Flow)
 */
public class OstSdk {

    private static Context mApplicationContext;

    public static Context getContext() {
        return mApplicationContext;
    }

    public static void init(Context context) {
        mApplicationContext = context.getApplicationContext();
        OstSdkDatabase.initDatabase(mApplicationContext);
        OstSdkKeyDatabase.initDatabase(mApplicationContext);
    }

    public static Economy registerEconomy(JSONObject jsonObject, @NonNull TaskCallback callback) throws JSONException {
        return ModelFactory.getEconomyModel().registerEconomy(jsonObject, callback);
    }

    public static Economy registerEconomy(JSONObject jsonObject) throws JSONException {
        return registerEconomy(jsonObject, new TaskCallback() {
        });
    }

    public static Economy getEconomy(String economyId) {
        return ModelFactory.getEconomyModel().getEconomyById(economyId);
    }

    public static User initUser(JSONObject jsonObject) throws JSONException {
        return initUser(jsonObject, new TaskCallback() {
        });
    }

    public static User initUser(JSONObject jsonObject, @NonNull TaskCallback callback) throws JSONException {
        return ModelFactory.getUserModel().initUser(jsonObject, callback);
    }

    public static User getUser(String id) {
        return ModelFactory.getUserModel().getUserById(id);
    }

    public static void delUser(String userId) {
        delUser(userId, new TaskCallback() {
        });
    }

    public static void delUser(String userId, @NonNull TaskCallback callback) {
        ModelFactory.getUserModel().deleteUser(userId, callback);
    }
}