package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstRuleDao;
import com.ost.mobilesdk.models.OstRuleModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstRule;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

class OstRuleModelRepository extends OstBaseModelCacheRepository implements OstRuleModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstRuleDao mOstRuleDao;

    OstRuleModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstRuleDao = db.ruleDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstRuleDao;
    }

    @Override
    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstRuleModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstRule getEntityById(String id) {
        return (OstRule)super.getById(id);
    }

    @Override
    public OstRule[] getEntitiesByParentId(String id) {
        return (OstRule[]) super.getByParentId(id);
    }

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstRuleModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstRuleModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}
