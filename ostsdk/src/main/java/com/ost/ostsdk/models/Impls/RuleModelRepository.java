package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.RuleDao;
import com.ost.ostsdk.models.RuleModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.OstRule;

import org.json.JSONException;
import org.json.JSONObject;

class RuleModelRepository extends BaseModelCacheRepository implements RuleModel {

    private static final int LRU_CACHE_SIZE = 5;
    private RuleDao mRuleDao;

    RuleModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mRuleDao = db.ruleDao();
    }


    @Override
    public void insertRule(final OstRule ostRule, final TaskCallback callback) {
        super.insert(ostRule, callback);
    }

    @Override
    public void insertAllRules(final OstRule[] ostRule, final TaskCallback callback) {
        super.insertAll(ostRule, callback);
    }

    @Override
    public void deleteRule(final String id, final TaskCallback callback) {
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
    public void deleteAllRules(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstRule initRule(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        OstRule ostRule = new OstRule(jsonObject);
        insert(ostRule, callback);
        return ostRule;
    }

    @Override
    BaseDao getModel() {
        return mRuleDao;
    }
}
