package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstSessionDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.TokenHolderSessionModel;
import com.ost.ostsdk.models.entities.OstSession;

import org.json.JSONException;
import org.json.JSONObject;

class TokenHolderSessionModelRepository extends BaseModelCacheRepository implements TokenHolderSessionModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstSessionDao mOstSessionDao;

    TokenHolderSessionModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstSessionDao = db.tokenHolderSessionDao();
    }


    @Override
    public void insertTokenHolderSession(final OstSession ostSession, final TaskCallback callback) {
        super.insert(ostSession, callback);
    }

    @Override
    public void insertAllTokenHolderSessions(final OstSession[] ostSession, final TaskCallback callback) {
        super.insertAll(ostSession, callback);
    }

    @Override
    public void deleteTokenHolderSession(final String id, final TaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public OstSession[] getTokenHolderSessionsByIds(String[] ids) {
        return (OstSession[]) super.getByIds(ids);
    }

    @Override
    public OstSession getTokenHolderSessionById(String id) {
        return (OstSession) super.getById(id);
    }

    @Override
    public void deleteAllTokenHolderSessions(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstSession initTokenHolderSession(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        OstSession multiSig = new OstSession(jsonObject);
        insert(multiSig, callback);
        return multiSig;
    }

    @Override
    public OstSession[] getTokenHolderSessionsByParentId(String id) {
        return (OstSession[]) super.getByParentId(id);
    }

    @Override
    OstBaseDao getModel() {
        return mOstSessionDao;
    }
}
