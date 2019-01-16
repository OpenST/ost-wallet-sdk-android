package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstDeviceDao;
import com.ost.ostsdk.models.MultiSigWalletModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.OstDevice;

import org.json.JSONException;
import org.json.JSONObject;

class MultiSigWalletModelRepository extends BaseModelCacheRepository implements MultiSigWalletModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceDao mOstDeviceDao;

    MultiSigWalletModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstDeviceDao = db.multiSigWalletDao();
    }


    @Override
    public void insertMultiSigWallet(final OstDevice ostDevice, final TaskCallback callback) {
        super.insert(ostDevice, callback);
    }

    @Override
    public void insertAllMultiSigWallets(final OstDevice[] ostDevice, final TaskCallback callback) {
        super.insertAll(ostDevice, callback);
    }

    @Override
    public void deleteMultiSigWallet(final String id, final TaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public OstDevice[] getMultiSigWalletsByIds(String[] ids) {
        return (OstDevice[]) super.getByIds(ids);
    }

    @Override
    public OstDevice getMultiSigWalletById(String id) {
        return (OstDevice) super.getById(id);
    }

    @Override
    public void deleteAllMultiSigWallets(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstDevice initMultiSigWallet(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        OstDevice ostDevice = new OstDevice(jsonObject);
        insert(ostDevice, callback);
        return ostDevice;
    }

    @Override
    public OstDevice[] getMultiSigWalletsByParentId(String id) {
        return (OstDevice[]) super.getByParentId(id);
    }

    @Override
    OstBaseDao getModel() {
        return mOstDeviceDao;
    }
}
