package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstTokenHolderDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.TokenHolderModel;
import com.ost.ostsdk.models.entities.OstTokenHolder;

import org.json.JSONException;
import org.json.JSONObject;

class TokenHolderModelRepository extends BaseModelCacheRepository implements TokenHolderModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstTokenHolderDao mOstTokenHolderDao;

    TokenHolderModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTokenHolderDao = db.tokenHolderDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTokenHolderDao;
    }

    @Override
    public void insertTokenHolder(OstTokenHolder ostTokenHolder, TaskCallback callback) {
        super.insert(ostTokenHolder, callback);
    }

    @Override
    public void insertAllTokenHolders(OstTokenHolder[] ostTokenHolders, TaskCallback callback) {
        super.insertAll(ostTokenHolders, callback);
    }

    @Override
    public void deleteTokenHolder(String id, TaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public OstTokenHolder[] getTokenHoldersByIds(String[] ids) {
        return (OstTokenHolder[]) super.getByIds(ids);
    }

    @Override
    public OstTokenHolder getTokenHolderById(String id) {
        return (OstTokenHolder) super.getById(id);
    }

    @Override
    public void deleteAllTokenHolders(TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstTokenHolder initTokenHolder(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
        insert(ostTokenHolder, callback);
        return ostTokenHolder;
    }
}