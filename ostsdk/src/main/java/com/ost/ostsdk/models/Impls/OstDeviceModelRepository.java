package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstDeviceDao;
import com.ost.ostsdk.models.OstDeviceModel;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstDevice;

import org.json.JSONException;
import org.json.JSONObject;

class OstDeviceModelRepository extends OstBaseModelCacheRepository implements OstDeviceModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceDao mOstDeviceDao;

    OstDeviceModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstDeviceDao = db.multiSigWalletDao();
    }


    @Override
    public void insertMultiSigWallet(final OstDevice ostDevice, final OstTaskCallback callback) {
        super.insert(ostDevice, callback);
    }

    @Override
    public void insertAllMultiSigWallets(final OstDevice[] ostDevice, final OstTaskCallback callback) {
        super.insertAll(ostDevice, callback);
    }

    @Override
    public void deleteMultiSigWallet(final String id, final OstTaskCallback callback) {
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
    public void deleteAllMultiSigWallets(final OstTaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstDevice initMultiSigWallet(JSONObject jsonObject, OstTaskCallback callback) throws JSONException {
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
