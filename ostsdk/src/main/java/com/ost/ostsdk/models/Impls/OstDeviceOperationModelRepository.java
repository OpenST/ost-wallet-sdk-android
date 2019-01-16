package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstDeviceOperationDao;
import com.ost.ostsdk.models.OstDeviceOperationModel;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstDeviceOperation;

import org.json.JSONException;
import org.json.JSONObject;

class OstDeviceOperationModelRepository extends OstBaseModelCacheRepository implements OstDeviceOperationModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceOperationDao mMultiSigOperation;

    OstDeviceOperationModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mMultiSigOperation = db.multiSigOperationDao();
    }


    @Override
    public void insertMultiSigOperation(final OstDeviceOperation ostDeviceOperation) {
        super.insert(ostDeviceOperation, new OstTaskCallback() {});
    }

    @Override
    public void insertAllMultiSigOperations(final OstDeviceOperation[] ostDeviceOperation) {
        super.insertAll(ostDeviceOperation, new OstTaskCallback() {});
    }

    @Override
    public void deleteMultiSigOperation(final String id) {
        super.delete(id, new OstTaskCallback() {});
    }

    @Override
    public OstDeviceOperation[] getMultiSigOperationsByIds(String[] ids) {
        return (OstDeviceOperation[]) super.getByIds(ids);
    }

    @Override
    public OstDeviceOperation getMultiSigOperationById(String id) {
        return (OstDeviceOperation) super.getById(id);
    }

    @Override
    public void deleteAllMultiSigOperations() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstDeviceOperation initMultiSigOperation(JSONObject jsonObject) throws JSONException {
        OstDeviceOperation ostDeviceOperation = new OstDeviceOperation(jsonObject);
        insert(ostDeviceOperation, null);
        return ostDeviceOperation;
    }

    @Override
    OstBaseDao getModel() {
        return mMultiSigOperation;
    }
}
