package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstExecutableRuleDao;
import com.ost.ostsdk.models.OstExecutableRuleModel;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstExecutableRule;

import org.json.JSONException;
import org.json.JSONObject;

class OstExecutableRuleModelRepository extends OstBaseModelCacheRepository implements OstExecutableRuleModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstExecutableRuleDao mOstExecutableRuleDao;

    OstExecutableRuleModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstExecutableRuleDao = db.executableRuleDao();
    }


    @Override
    public void insertExecutableRule(final OstExecutableRule executableRule, final OstTaskCallback callback) {
        super.insert(executableRule, callback);
    }

    @Override
    public void insertAllExecutableRules(final OstExecutableRule[] executableRule, final OstTaskCallback callback) {
        super.insertAll(executableRule, callback);
    }

    @Override
    public void deleteExecutableRule(final String id, final OstTaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public OstExecutableRule[] getExecutableRulesByIds(String[] ids) {
        return (OstExecutableRule[]) super.getByIds(ids);
    }

    @Override
    public OstExecutableRule getExecutableRuleById(String id) {
        return (OstExecutableRule) super.getById(id);
    }

    @Override
    public void deleteAllExecutableRules(final OstTaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstExecutableRule initExecutableRule(JSONObject jsonObject) throws JSONException {
        OstExecutableRule executableRule = new OstExecutableRule(jsonObject);
        insert(executableRule, null);
        return executableRule;
    }

    @Override
    OstBaseDao getModel() {
        return mOstExecutableRuleDao;
    }
}
