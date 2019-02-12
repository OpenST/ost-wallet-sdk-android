package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstTokenDao;
import com.ost.mobilesdk.models.OstCreditsModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstCredits;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

class OstCreditsModelRepository extends OstBaseModelCacheRepository implements OstCreditsModel {

    private OstTokenDao mOstTokenDao;

    OstCreditsModelRepository() {
        super(5);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTokenDao = db.tokenDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTokenDao;
    }

    @Override
    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstCreditsModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstCredits getEntityById(String id) {
        return (OstCredits) super.getById(id);
    }

    @Override
    public OstCredits[] getEntitiesByParentId(String id) {
        return (OstCredits[]) super.getByParentId(id);
    }

    @Override
    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstCreditsModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });

    }

    @Override
    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstCreditsModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }
}