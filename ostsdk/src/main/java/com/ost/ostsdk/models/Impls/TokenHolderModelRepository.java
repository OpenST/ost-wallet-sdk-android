package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.TokenHolderDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.TokenHolderModel;
import com.ost.ostsdk.models.entities.TokenHolder;

import org.json.JSONException;
import org.json.JSONObject;

class TokenHolderModelRepository extends BaseModelCacheRepository implements TokenHolderModel {

    private static final int LRU_CACHE_SIZE = 5;
    private TokenHolderDao mTokenHolderDao;

    TokenHolderModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mTokenHolderDao = db.tokenHolderDao();
    }

    @Override
    BaseDao getModel() {
        return mTokenHolderDao;
    }

    @Override
    public void insertTokenHolder(TokenHolder tokenHolder, TaskCallback callback) {
        super.insert(tokenHolder, callback);
    }

    @Override
    public void insertAllTokenHolders(TokenHolder[] tokenHolders, TaskCallback callback) {
        super.insertAll(tokenHolders, callback);
    }

    @Override
    public void deleteTokenHolder(TokenHolder tokenHolder, TaskCallback callback) {
        super.delete(tokenHolder, callback);
    }

    @Override
    public TokenHolder[] getTokenHoldersByIds(String[] ids) {
        return (TokenHolder[]) super.getByIds(ids);
    }

    @Override
    public TokenHolder getTokenHolderById(String id) {
        return (TokenHolder) super.getById(id);
    }

    @Override
    public void deleteAllTokenHolders(TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public TokenHolder initTokenHolder(JSONObject jsonObject) throws JSONException {
        TokenHolder tokenHolder = new TokenHolder(jsonObject);
        insert(tokenHolder, null);
        return tokenHolder;
    }
}