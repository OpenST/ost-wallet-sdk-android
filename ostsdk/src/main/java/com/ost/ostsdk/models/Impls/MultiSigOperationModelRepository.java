package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstDeviceOperationDao;
import com.ost.ostsdk.models.MultiSigOperationModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.OstDeviceOperation;

import org.json.JSONException;
import org.json.JSONObject;

class MultiSigOperationModelRepository extends BaseModelCacheRepository implements MultiSigOperationModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceOperationDao mMultiSigOperation;

    MultiSigOperationModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mMultiSigOperation = db.multiSigOperationDao();
    }


    @Override
    public void insertMultiSigOperation(final OstDeviceOperation ostDeviceOperation, final TaskCallback callback) {
        super.insert(ostDeviceOperation, callback);
    }

    @Override
    public void insertAllMultiSigOperations(final OstDeviceOperation[] ostDeviceOperation, final TaskCallback callback) {
        super.insertAll(ostDeviceOperation, callback);
    }

    @Override
    public void deleteMultiSigOperation(final String id, final TaskCallback callback) {
        super.delete(id, callback);
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
    public void deleteAllMultiSigOperations(final TaskCallback callback) {
        super.deleteAll(callback);
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
