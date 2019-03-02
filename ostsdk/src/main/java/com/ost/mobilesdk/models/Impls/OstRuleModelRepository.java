package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstRuleDao;
import com.ost.mobilesdk.models.OstRuleModel;
import com.ost.mobilesdk.models.entities.OstRule;

import org.web3j.crypto.Keys;

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
    public OstRule getEntityById(String id) {
        return (OstRule)super.getById(Keys.toChecksumAddress(id));
    }

    @Override
    public OstRule[] getEntitiesByParentId(String id) {
        return (OstRule[]) super.getByParentId(id);
    }
}
