package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstSessionDao;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.OstSessionModel;
import com.ost.ostsdk.models.entities.OstSession;

import org.json.JSONException;
import org.json.JSONObject;

class OstSessionModelRepository extends OstBaseModelCacheRepository implements OstSessionModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstSessionDao mOstSessionDao;

    OstSessionModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstSessionDao = db.tokenHolderSessionDao();
    }


    @Override
    public void insertTokenHolderSession(final OstSession ostSession) {
        super.insert(ostSession, new OstTaskCallback() {});
    }

    @Override
    public void insertAllTokenHolderSessions(final OstSession[] ostSession) {
        super.insertAll(ostSession, new OstTaskCallback() {});
    }

    @Override
    public void deleteTokenHolderSession(final String id) {
        super.delete(id, new OstTaskCallback() {});
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
    public void deleteAllTokenHolderSessions() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstSession initTokenHolderSession(JSONObject jsonObject) throws JSONException {
        OstSession multiSig = new OstSession(jsonObject);
        insert(multiSig, new OstTaskCallback() {});
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
