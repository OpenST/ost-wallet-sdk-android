package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstSessionDao;
import com.ost.mobilesdk.models.OstSessionModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

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
    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstSessionModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstSession getEntityById(String id) {
        return (OstSession)super.getById(id);
    }

    @Override
    public OstSession[] getEntitiesByParentId(String id) {
        return (OstSession[]) super.getByParentId(id);
    }

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstSessionModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstSessionModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}
