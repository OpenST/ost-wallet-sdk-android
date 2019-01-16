package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.TokenHolderSessionDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.TokenHolderSessionModel;
import com.ost.ostsdk.models.entities.OstTokenHolderSession;

import org.json.JSONException;
import org.json.JSONObject;

class TokenHolderSessionModelRepository extends BaseModelCacheRepository implements TokenHolderSessionModel {

    private static final int LRU_CACHE_SIZE = 5;
    private TokenHolderSessionDao mTokenHolderSessionDao;

    TokenHolderSessionModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mTokenHolderSessionDao = db.tokenHolderSessionDao();
    }


    @Override
    public void insertTokenHolderSession(final OstTokenHolderSession ostTokenHolderSession, final TaskCallback callback) {
        super.insert(ostTokenHolderSession, callback);
    }

    @Override
    public void insertAllTokenHolderSessions(final OstTokenHolderSession[] ostTokenHolderSession, final TaskCallback callback) {
        super.insertAll(ostTokenHolderSession, callback);
    }

    @Override
    public void deleteTokenHolderSession(final String id, final TaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public OstTokenHolderSession[] getTokenHolderSessionsByIds(String[] ids) {
        return (OstTokenHolderSession[]) super.getByIds(ids);
    }

    @Override
    public OstTokenHolderSession getTokenHolderSessionById(String id) {
        return (OstTokenHolderSession) super.getById(id);
    }

    @Override
    public void deleteAllTokenHolderSessions(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstTokenHolderSession initTokenHolderSession(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        OstTokenHolderSession multiSig = new OstTokenHolderSession(jsonObject);
        insert(multiSig, callback);
        return multiSig;
    }

    @Override
    public OstTokenHolderSession[] getTokenHolderSessionsByParentId(String id) {
        return (OstTokenHolderSession[]) super.getByParentId(id);
    }

    @Override
    BaseDao getModel() {
        return mTokenHolderSessionDao;
    }
}
