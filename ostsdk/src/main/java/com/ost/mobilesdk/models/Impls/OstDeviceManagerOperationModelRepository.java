package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstDeviceOperationDao;
import com.ost.mobilesdk.models.OstDeviceManagerOperationModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

class OstDeviceManagerOperationModelRepository extends OstBaseModelCacheRepository implements OstDeviceManagerOperationModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceOperationDao mMultiSigOperation;

    OstDeviceManagerOperationModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mMultiSigOperation = db.multiSigOperationDao();
    }


    @Override
    OstBaseDao getModel() {
        return mMultiSigOperation;
    }

    @Override
    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceManagerOperationModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstDeviceManagerOperation getEntityById(String id) {
        return (OstDeviceManagerOperation)super.getById(id);
    }

    @Override
    public OstDeviceManagerOperation[] getEntitiesByParentId(String id) {
        return (OstDeviceManagerOperation[]) super.getByParentId(id);
    }

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceManagerOperationModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstDeviceManagerOperationModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}
