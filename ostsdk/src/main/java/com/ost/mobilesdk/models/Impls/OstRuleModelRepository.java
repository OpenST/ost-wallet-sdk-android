package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstRuleDao;
import com.ost.mobilesdk.models.OstRuleModel;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstRule;

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
    public void insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        super.insert(ostBaseEntity, new OstTaskCallback() {});
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
    public void deleteEntity(String id) {
        super.delete(id, new OstTaskCallback() {});
    }

    @Override
    public void deleteAllEntities() {
        super.deleteAll(new OstTaskCallback() {});
    }
}
