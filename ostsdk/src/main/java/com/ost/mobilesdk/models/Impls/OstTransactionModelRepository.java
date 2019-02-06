package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstTransactionDao;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.models.OstTransactionModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstTransaction;

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
    public void insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        super.insert(ostBaseEntity, new OstTaskCallback() {});
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
    public void deleteEntity(String id) {
        super.delete(id, new OstTaskCallback() {});
    }

    @Override
    public void deleteAllEntities() {
        super.deleteAll(new OstTaskCallback() {});
    }
}
