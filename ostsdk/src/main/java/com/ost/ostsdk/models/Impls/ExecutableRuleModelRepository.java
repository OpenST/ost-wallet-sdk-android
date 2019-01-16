package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.ExecutableRuleDao;
import com.ost.ostsdk.models.ExecutableRuleModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.OstExecutableRule;

import org.json.JSONException;
import org.json.JSONObject;

class ExecutableRuleModelRepository extends BaseModelCacheRepository implements ExecutableRuleModel {

    private static final int LRU_CACHE_SIZE = 5;
    private ExecutableRuleDao mExecutableRuleDao;

    ExecutableRuleModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mExecutableRuleDao = db.executableRuleDao();
    }


    @Override
    public void insertExecutableRule(final OstExecutableRule executableRule, final TaskCallback callback) {
        super.insert(executableRule, callback);
    }

    @Override
    public void insertAllExecutableRules(final OstExecutableRule[] executableRule, final TaskCallback callback) {
        super.insertAll(executableRule, callback);
    }

    @Override
    public void deleteExecutableRule(final String id, final TaskCallback callback) {
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
    public void deleteAllExecutableRules(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstExecutableRule initExecutableRule(JSONObject jsonObject) throws JSONException {
        OstExecutableRule executableRule = new OstExecutableRule(jsonObject);
        insert(executableRule, null);
        return executableRule;
    }

    @Override
    BaseDao getModel() {
        return mExecutableRuleDao;
    }
}
