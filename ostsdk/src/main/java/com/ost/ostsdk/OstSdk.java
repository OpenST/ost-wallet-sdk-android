package com.ost.ostsdk;

import android.content.Context;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.models.EconomyModel;
import com.ost.ostsdk.models.Impls.EconomyModelRepository;
import com.ost.ostsdk.models.Impls.UserModelRepository;
import com.ost.ostsdk.models.UserModel;

public class OstSdk {

    private static Context mApplicationContext;

    public static void init(Context context) {
        mApplicationContext = context.getApplicationContext();
        OstSdkDatabase.initDatabase(mApplicationContext);
    }

    public static EconomyModel getEconomyModel() {
        return EconomyModelRepository.getInstance();
    }

    public static UserModel getUserModel() {
        return UserModelRepository.getInstance();
    }
}