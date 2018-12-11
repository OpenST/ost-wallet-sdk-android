package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.EconomyDao;
import com.ost.ostsdk.models.EconomyModel;
import com.ost.ostsdk.models.entities.Economy;

import org.json.JSONObject;

class EconomyModelRepository implements EconomyModel {

    private static volatile EconomyModel INSTANCE;

    private EconomyDao mEconomyDao;

    EconomyModelRepository() {
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mEconomyDao = db.economyDao();
    }


    @Override
    public Economy registerEconomy(JSONObject jsonObject) {
        return null;
    }

    @Override
    public Economy getEconomyById(String id) {
        return null;
    }
}