package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.MultiSigOperationDao;
import com.ost.ostsdk.models.MultiSigOperationModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.MultiSigOperation;

import org.json.JSONException;
import org.json.JSONObject;

class MultiSigOperationModelRepository extends BaseModelCacheRepository implements MultiSigOperationModel {

    private static final int LRU_CACHE_SIZE = 5;
    private MultiSigOperationDao mMultiSigOperation;

    MultiSigOperationModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mMultiSigOperation = db.multiSigOperationDao();
    }


    @Override
    public void insertMultiSigOperation(final MultiSigOperation multiSigOperation, final TaskCallback callback) {
        super.insert(multiSigOperation, callback);
    }

    @Override
    public void insertAllMultiSigOperations(final MultiSigOperation[] multiSigOperation, final TaskCallback callback) {
        super.insertAll(multiSigOperation, callback);
    }

    @Override
    public void deleteMultiSigOperation(final MultiSigOperation multiSigOperation, final TaskCallback callback) {
        super.delete(multiSigOperation, callback);
    }

    @Override
    public MultiSigOperation[] getMultiSigOperationsByIds(String[] ids) {
        return (MultiSigOperation[]) super.getByIds(ids);
    }

    @Override
    public MultiSigOperation getMultiSigOperationById(String id) {
        return (MultiSigOperation) super.getById(id);
    }

    @Override
    public void deleteAllMultiSigOperations(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public MultiSigOperation initMultiSigOperation(JSONObject jsonObject) throws JSONException {
        MultiSigOperation multiSigOperation = new MultiSigOperation(jsonObject);
        insert(multiSigOperation, null);
        return multiSigOperation;
    }

    @Override
    BaseDao getModel() {
        return mMultiSigOperation;
    }
}
