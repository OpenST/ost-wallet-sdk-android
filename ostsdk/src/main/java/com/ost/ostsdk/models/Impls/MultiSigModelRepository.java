package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.MultiSigDao;
import com.ost.ostsdk.models.MultiSigModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.MultiSig;

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
    public void insertMultiSig(final MultiSig multiSig, final TaskCallback callback) {
        super.insert(multiSig, callback);
    }

    @Override
    public void insertAllMultiSigs(final MultiSig[] multiSig, final TaskCallback callback) {
        super.insertAll(multiSig, callback);
    }

    @Override
    public void deleteMultiSig(final String id, final TaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public MultiSig[] getMultiSigsByIds(String[] ids) {
        return (MultiSig[]) super.getByIds(ids);
    }

    @Override
    public MultiSig getMultiSigById(String id) {
        return (MultiSig) super.getById(id);
    }

    @Override
    public void deleteAllMultiSigs(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public MultiSig initMultiSig(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        MultiSig multiSig = new MultiSig(jsonObject);
        insert(multiSig, callback);
        return multiSig;
    }

    @Override
    BaseDao getModel() {
        return mMultiSigDao;
    }
}
