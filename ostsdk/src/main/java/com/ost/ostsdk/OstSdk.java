package com.ost.ostsdk;

import android.content.Context;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.models.EconomyModel;
import com.ost.ostsdk.models.Impls.UserModelRepository;
import com.ost.ostsdk.models.UserModel;
import com.ost.ostsdk.models.entities.Economy;
import com.ost.ostsdk.models.entities.User;

import org.json.JSONObject;

public class OstSdk {

    private UserModel mUserModel;
    private EconomyModel mEconomyModel;

    public void init(Context context) {
        OstSdkDatabase.getDatabase(context);
        mUserModel = new UserModelRepository(context);
    }

    public Economy registerEconomy(JSONObject jsonObject) {
        return mEconomyModel.registerEconomy(jsonObject);
    }

    public User initUser(JSONObject jsonObject) {
        return mUserModel.initUser(jsonObject);
    }
}
