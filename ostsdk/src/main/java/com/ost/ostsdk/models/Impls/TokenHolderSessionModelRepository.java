package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.TokenHolderSessionDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.TokenHolderSessionModel;
import com.ost.ostsdk.models.entities.TokenHolderSession;

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
    public void insertTokenHolderSession(final TokenHolderSession tokenHolderSession, final TaskCallback callback) {
        super.insert(tokenHolderSession, callback);
    }

    @Override
    public void insertAllTokenHolderSessions(final TokenHolderSession[] tokenHolderSession, final TaskCallback callback) {
        super.insertAll(tokenHolderSession, callback);
    }

    @Override
    public void deleteTokenHolderSession(final String id, final TaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public TokenHolderSession[] getTokenHolderSessionsByIds(String[] ids) {
        return (TokenHolderSession[]) super.getByIds(ids);
    }

    @Override
    public TokenHolderSession getTokenHolderSessionById(String id) {
        return (TokenHolderSession) super.getById(id);
    }

    @Override
    public void deleteAllTokenHolderSessions(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public TokenHolderSession initTokenHolderSession(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        TokenHolderSession multiSig = new TokenHolderSession(jsonObject);
        insert(multiSig, callback);
        return multiSig;
    }

    @Override
    public TokenHolderSession[] getTokenHolderSessionsByParentId(String id) {
        return (TokenHolderSession[]) super.getByParentId(id);
    }

    @Override
    BaseDao getModel() {
        return mTokenHolderSessionDao;
    }
}
