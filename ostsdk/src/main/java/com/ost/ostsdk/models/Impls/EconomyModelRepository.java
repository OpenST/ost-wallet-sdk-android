package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.EconomyDao;
import com.ost.ostsdk.models.EconomyModel;
import com.ost.ostsdk.models.entities.Economy;

import org.json.JSONObject;

class EconomyModelRepository extends BaseModelCacheRepository implements EconomyModel {

    private EconomyDao mEconomyDao;

    EconomyModelRepository() {
        super(5);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mEconomyDao = db.economyDao();
    }

    @Override
    BaseDao getModel() {
        return mEconomyDao;
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