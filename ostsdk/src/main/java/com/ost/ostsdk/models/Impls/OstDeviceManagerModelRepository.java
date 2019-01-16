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
    public void insertMultiSig(final OstDeviceManager ostDeviceManager) {
        super.insert(ostDeviceManager, new OstTaskCallback() {});
    }

    @Override
    public void insertAllMultiSigs(final OstDeviceManager[] ostDeviceManager) {
        super.insertAll(ostDeviceManager, new OstTaskCallback() {});
    }

    @Override
    public void deleteMultiSig(final String id) {
        super.delete(id, new OstTaskCallback() {});
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
    public void deleteAllMultiSigs() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstDeviceManager initMultiSig(JSONObject jsonObject) throws JSONException {
        OstDeviceManager ostDeviceManager = new OstDeviceManager(jsonObject);
        insert(ostDeviceManager, new OstTaskCallback() {});
        return ostDeviceManager;
    }

    @Override
    OstBaseDao getModel() {
        return mOstDeviceManagerDao;
    }
}
