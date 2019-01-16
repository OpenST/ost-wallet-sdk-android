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
    public void insertRule(final OstRule ostRule) {
        super.insert(ostRule, new OstTaskCallback() {});
    }

    @Override
    public void insertAllRules(final OstRule[] ostRule) {
        super.insertAll(ostRule, new OstTaskCallback() {});
    }

    @Override
    public void deleteRule(final String id) {
        super.delete(id, new OstTaskCallback() {});
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
    public void deleteAllRules() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstRule initRule(JSONObject jsonObject) throws JSONException {
        OstRule ostRule = new OstRule(jsonObject);
        insert(ostRule, new OstTaskCallback() {});
        return ostRule;
    }

    @Override
    OstBaseDao getModel() {
        return mOstRuleDao;
    }
}
