package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstDeviceManagerDao;
import com.ost.ostsdk.models.OstDeviceManagerModel;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstDeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

class OstDeviceManagerModelRepository extends OstBaseModelCacheRepository implements OstDeviceManagerModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceManagerDao mOstDeviceManagerDao;

    OstDeviceManagerModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstDeviceManagerDao = db.multiSigDao();
    }


    @Override
    public void insertMultiSig(final OstDeviceManager ostDeviceManager, final OstTaskCallback callback) {
        super.insert(ostDeviceManager, callback);
    }

    @Override
    public void insertAllMultiSigs(final OstDeviceManager[] ostDeviceManager, final OstTaskCallback callback) {
        super.insertAll(ostDeviceManager, callback);
    }

    @Override
    public void deleteMultiSig(final String id, final OstTaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public OstDeviceManager[] getMultiSigsByIds(String[] ids) {
        return (OstDeviceManager[]) super.getByIds(ids);
    }

    @Override
    public OstDeviceManager getMultiSigById(String id) {
        return (OstDeviceManager) super.getById(id);
    }

    @Override
    public void deleteAllMultiSigs(final OstTaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstDeviceManager initMultiSig(JSONObject jsonObject, OstTaskCallback callback) throws JSONException {
        OstDeviceManager ostDeviceManager = new OstDeviceManager(jsonObject);
        insert(ostDeviceManager, callback);
        return ostDeviceManager;
    }

    @Override
    OstBaseDao getModel() {
        return mOstDeviceManagerDao;
    }
}
