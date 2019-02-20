package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstSessionDao;
import com.ost.mobilesdk.models.OstSessionModel;
import com.ost.mobilesdk.models.entities.OstSession;

class OstSessionModelRepository extends OstBaseModelCacheRepository implements OstSessionModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstSessionDao mOstSessionDao;

    OstSessionModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstSessionDao = db.tokenHolderSessionDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstSessionDao;
    }

    @Override
    public OstSession getEntityById(String id) {
        return (OstSession)super.getById(id);
    }

    @Override
    public OstSession[] getEntitiesByParentId(String id) {
        return (OstSession[]) super.getByParentId(id);
    }
}
