package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstTokenHolderDao;
import com.ost.mobilesdk.models.OstTokenHolderModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstTokenHolder;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

class OstTokenHolderModelRepository extends OstBaseModelCacheRepository implements OstTokenHolderModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstTokenHolderDao mOstTokenHolderDao;

    OstTokenHolderModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTokenHolderDao = db.tokenHolderDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTokenHolderDao;
    }


    @Override
    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstTokenHolderModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstTokenHolder getEntityById(String id) {
        return (OstTokenHolder)super.getById(id);
    }

    @Override
    public OstTokenHolder[] getEntitiesByParentId(String id) {
        return (OstTokenHolder[]) super.getByParentId(id);
    }

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstTokenHolderModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstTokenHolderModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}