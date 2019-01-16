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
    public void insertMultiSigWallet(final OstDevice ostDevice) {
        super.insert(ostDevice, new OstTaskCallback() {});
    }

    @Override
    public void insertAllMultiSigWallets(final OstDevice[] ostDevice) {
        super.insertAll(ostDevice, new OstTaskCallback() {});
    }

    @Override
    public void deleteMultiSigWallet(final String id) {
        super.delete(id, new OstTaskCallback() {});
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
    public void deleteAllMultiSigWallets() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstDevice initMultiSigWallet(JSONObject jsonObject) throws JSONException {
        OstDevice ostDevice = new OstDevice(jsonObject);
        insert(ostDevice, new OstTaskCallback() {});
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
