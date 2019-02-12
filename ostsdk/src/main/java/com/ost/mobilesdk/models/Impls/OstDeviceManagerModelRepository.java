package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstDeviceManagerDao;
import com.ost.mobilesdk.models.OstDeviceManagerModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstDeviceManager;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

class OstDeviceManagerModelRepository extends OstBaseModelCacheRepository implements OstDeviceManagerModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceManagerDao mOstDeviceManagerDao;

    OstDeviceManagerModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstDeviceManagerDao = db.multiSigDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstDeviceManagerDao;
    }

    @Override
    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceManagerModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstDeviceManager getEntityById(String id) {
        return (OstDeviceManager)super.getById(id);
    }

    @Override
    public OstDeviceManager[] getEntitiesByParentId(String id) {
        return (OstDeviceManager[]) super.getByParentId(id);
    }

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceManagerModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceManagerModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}
