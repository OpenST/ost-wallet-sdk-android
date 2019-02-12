package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstTransactionDao;
import com.ost.mobilesdk.models.OstTransactionModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstTransaction;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

class OstTransactionModelRepository extends OstBaseModelCacheRepository implements OstTransactionModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstTransactionDao mOstTransactionDao;

    OstTransactionModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTransactionDao = db.executableRuleDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTransactionDao;
    }

    @Override
    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstTransactionModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstTransaction getEntityById(String id) {
        return (OstTransaction)super.getById(id);
    }

    @Override
    public OstTransaction[] getEntitiesByParentId(String id) {
        return (OstTransaction[]) super.getByParentId(id);
    }

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstTransactionModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstTransactionModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}
