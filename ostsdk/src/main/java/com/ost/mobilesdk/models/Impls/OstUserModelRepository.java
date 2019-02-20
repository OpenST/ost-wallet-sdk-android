package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstUserDao;
import com.ost.mobilesdk.models.OstUserModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

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

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstUserModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstUserModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}
