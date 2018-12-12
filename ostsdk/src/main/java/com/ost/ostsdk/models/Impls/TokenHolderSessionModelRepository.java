package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.TokenHolderSessionDao;
import com.ost.ostsdk.models.TokenHolderSessionModel;
import com.ost.ostsdk.models.TaskCompleteCallback;
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
    public void insertTokenHolderSession(final TokenHolderSession tokenHolderSession, final TaskCompleteCallback callback) {
        super.insert(tokenHolderSession, callback);
    }

    @Override
    public void insertAllTokenHolderSessions(final TokenHolderSession[] tokenHolderSession, final TaskCompleteCallback callback) {
        super.insertAll(tokenHolderSession, callback);
    }

    @Override
    public void deleteTokenHolderSession(final TokenHolderSession tokenHolderSession, final TaskCompleteCallback callback) {
        super.delete(tokenHolderSession, callback);
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
    public void deleteAllTokenHolderSessions(final TaskCompleteCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public TokenHolderSession initTokenHolderSession(JSONObject jsonObject) throws JSONException {
        TokenHolderSession multiSig = new TokenHolderSession(jsonObject);
        insert(multiSig, null);
        return multiSig;
    }

    @Override
    BaseDao getModel() {
        return mTokenHolderSessionDao;
    }
}
