package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.RuleDao;
import com.ost.ostsdk.models.RuleModel;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.entities.Rule;

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
    public void insertRule(final Rule rule, final TaskCompleteCallback callback) {
        super.insert(rule, callback);
    }

    @Override
    public void insertAllRules(final Rule[] rule, final TaskCompleteCallback callback) {
        super.insertAll(rule, callback);
    }

    @Override
    public void deleteRule(final Rule rule, final TaskCompleteCallback callback) {
        super.delete(rule, callback);
    }

    @Override
    public Rule[] getRulesByIds(String[] ids) {
        return (Rule[]) super.getByIds(ids);
    }

    @Override
    public Rule getRuleById(String id) {
        return (Rule) super.getById(id);
    }

    @Override
    public void deleteAllRules(final TaskCompleteCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public Rule initRule(JSONObject jsonObject) throws JSONException {
        Rule rule = new Rule(jsonObject);
        insert(rule, null);
        return rule;
    }

    @Override
    BaseDao getModel() {
        return mRuleDao;
    }
}
