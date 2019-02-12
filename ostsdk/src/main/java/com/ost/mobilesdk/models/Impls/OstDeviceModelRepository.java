package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstDeviceDao;
import com.ost.mobilesdk.models.OstDeviceModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

class OstDeviceModelRepository extends OstBaseModelCacheRepository implements OstDeviceModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceDao mOstDeviceDao;

    OstDeviceModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstDeviceDao = db.multiSigWalletDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstDeviceDao;
    }

    @Override
    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstDevice getEntityById(String id) {
        return (OstDevice)super.getById(id);
    }

    @Override
    public OstDevice[] getEntitiesByParentId(String id) {
        return (OstDevice[]) super.getByParentId(id);
    }

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}