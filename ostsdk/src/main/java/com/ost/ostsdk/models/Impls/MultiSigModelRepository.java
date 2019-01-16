package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.MultiSigDao;
import com.ost.ostsdk.models.MultiSigModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.OstDeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

class MultiSigModelRepository extends BaseModelCacheRepository implements MultiSigModel {

    private static final int LRU_CACHE_SIZE = 5;
    private MultiSigDao mMultiSigDao;

    MultiSigModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mMultiSigDao = db.multiSigDao();
    }


    @Override
    public void insertMultiSig(final OstDeviceManager ostDeviceManager, final TaskCallback callback) {
        super.insert(ostDeviceManager, callback);
    }

    @Override
    public void insertAllMultiSigs(final OstDeviceManager[] ostDeviceManager, final TaskCallback callback) {
        super.insertAll(ostDeviceManager, callback);
    }

    @Override
    public void deleteMultiSig(final String id, final TaskCallback callback) {
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
    public void deleteAllMultiSigs(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstDeviceManager initMultiSig(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        OstDeviceManager ostDeviceManager = new OstDeviceManager(jsonObject);
        insert(ostDeviceManager, callback);
        return ostDeviceManager;
    }

    @Override
    BaseDao getModel() {
        return mMultiSigDao;
    }
}
