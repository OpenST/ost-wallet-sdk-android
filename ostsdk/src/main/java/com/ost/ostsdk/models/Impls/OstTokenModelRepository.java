package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstTokenDao;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.OstTokenModel;
import com.ost.ostsdk.models.entities.OstToken;

class OstTokenModelRepository extends OstBaseModelCacheRepository implements OstTokenModel {

    private OstTokenDao mOstTokenDao;

    OstTokenModelRepository() {
        super(5);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTokenDao = db.tokenDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTokenDao;
    }


    @Override
    public OstToken insert(OstToken ostToken) {
        super.insert(ostToken, new OstTaskCallback() {});
        return ostToken;
    }

    @Override
    public OstToken getTokenById(String id) {
        return (OstToken) super.getById(id);
    }
}