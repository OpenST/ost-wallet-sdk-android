package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstRuleDao;
import com.ost.ostsdk.models.OstRuleModel;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstRule;

import org.json.JSONException;
import org.json.JSONObject;

class OstRuleModelRepository extends OstBaseModelCacheRepository implements OstRuleModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstRuleDao mOstRuleDao;

    OstRuleModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstRuleDao = db.ruleDao();
    }


    @Override
    public void insertRule(final OstRule ostRule, final OstTaskCallback callback) {
        super.insert(ostRule, callback);
    }

    @Override
    public void insertAllRules(final OstRule[] ostRule, final OstTaskCallback callback) {
        super.insertAll(ostRule, callback);
    }

    @Override
    public void deleteRule(final String id, final OstTaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public OstRule[] getRulesByIds(String[] ids) {
        return (OstRule[]) super.getByIds(ids);
    }

    @Override
    public OstRule getRuleById(String id) {
        return (OstRule) super.getById(id);
    }

    @Override
    public void deleteAllRules(final OstTaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstRule initRule(JSONObject jsonObject, OstTaskCallback callback) throws JSONException {
        OstRule ostRule = new OstRule(jsonObject);
        insert(ostRule, callback);
        return ostRule;
    }

    @Override
    OstBaseDao getModel() {
        return mOstRuleDao;
    }
}
