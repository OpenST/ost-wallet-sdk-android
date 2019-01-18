package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstDeviceOperationDao;
import com.ost.ostsdk.models.OstDeviceManagerOperationModel;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstDeviceManagerOperation;

import org.json.JSONException;
import org.json.JSONObject;

class OstDeviceManagerOperationModelRepository extends OstBaseModelCacheRepository implements OstDeviceManagerOperationModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceOperationDao mMultiSigOperation;

    OstDeviceManagerOperationModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mMultiSigOperation = db.multiSigOperationDao();
    }


    @Override
    public void insertMultiSigOperation(final OstDeviceManagerOperation ostDeviceManagerOperation) {
        super.insert(ostDeviceManagerOperation, new OstTaskCallback() {});
    }

    @Override
    public void insertAllMultiSigOperations(final OstDeviceManagerOperation[] ostDeviceManagerOperation) {
        super.insertAll(ostDeviceManagerOperation, new OstTaskCallback() {});
    }

    @Override
    public void deleteMultiSigOperation(final String id) {
        super.delete(id, new OstTaskCallback() {});
    }

    @Override
    public OstDeviceManagerOperation[] getMultiSigOperationsByIds(String[] ids) {
        return (OstDeviceManagerOperation[]) super.getByIds(ids);
    }

    @Override
    public OstDeviceManagerOperation getMultiSigOperationById(String id) {
        return (OstDeviceManagerOperation) super.getById(id);
    }

    @Override
    public void deleteAllMultiSigOperations() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstDeviceManagerOperation insert(OstDeviceManagerOperation ostDeviceManagerOperation) {
        insert(ostDeviceManagerOperation, null);
        return ostDeviceManagerOperation;
    }

    @Override
    OstBaseDao getModel() {
        return mMultiSigOperation;
    }
}
