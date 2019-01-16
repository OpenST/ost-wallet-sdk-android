package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstTokenHolderDao;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.OstTokenHolderModel;
import com.ost.ostsdk.models.entities.OstTokenHolder;

import org.json.JSONException;
import org.json.JSONObject;

class OstTokenHolderModelRepository extends OstBaseModelCacheRepository implements OstTokenHolderModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstTokenHolderDao mOstTokenHolderDao;

    OstTokenHolderModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTokenHolderDao = db.tokenHolderDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTokenHolderDao;
    }

    @Override
    public void insertTokenHolder(OstTokenHolder ostTokenHolder, OstTaskCallback callback) {
        super.insert(ostTokenHolder, callback);
    }

    @Override
    public void insertAllTokenHolders(OstTokenHolder[] ostTokenHolders, OstTaskCallback callback) {
        super.insertAll(ostTokenHolders, callback);
    }

    @Override
    public void deleteTokenHolder(String id, OstTaskCallback callback) {
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
    public void deleteAllTokenHolders(OstTaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstTokenHolder initTokenHolder(JSONObject jsonObject, OstTaskCallback callback) throws JSONException {
        OstTokenHolder ostTokenHolder = new OstTokenHolder(jsonObject);
        insert(ostTokenHolder, callback);
        return ostTokenHolder;
    }
}