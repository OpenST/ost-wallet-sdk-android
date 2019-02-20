package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstUserDao;
import com.ost.mobilesdk.models.OstUserModel;
import com.ost.mobilesdk.models.entities.OstUser;

class OstUserModelRepository extends OstBaseModelCacheRepository implements OstUserModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstUserDao mOstUserDao;

    OstUserModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstUserDao = db.userDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstUserDao;
    }

    

    @Override
    public OstUser getEntityById(String id) {
        return (OstUser) super.getById(id);
    }

    @Override
    public OstUser[] getEntitiesByParentId(String id) {
        return (OstUser[]) super.getByParentId(id);
    }
}
