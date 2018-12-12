package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.SecureKeyDao;
import com.ost.ostsdk.models.SecureKeyModel;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.entities.SecureKey;

import org.json.JSONException;
import org.json.JSONObject;

class SecureKeyModelRepository extends BaseModelRepository implements SecureKeyModel {

    private SecureKeyDao mSecureKeyDao;

    SecureKeyModelRepository() {
        super();
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mSecureKeyDao = db.secureKeyDao();
    }


    @Override
    public void insertSecureKey(final SecureKey secureKey, final TaskCompleteCallback callback) {
        super.insert(secureKey, callback);
    }

    @Override
    public void insertAllSecureKeys(final SecureKey[] secureKey, final TaskCompleteCallback callback) {
        super.insertAll(secureKey, callback);
    }

    @Override
    public void deleteSecureKey(final SecureKey secureKey, final TaskCompleteCallback callback) {
        super.delete(secureKey, callback);
    }

    @Override
    public SecureKey[] getSecureKeysByIds(String[] ids) {
        return (SecureKey[]) super.getByIds(ids);
    }

    @Override
    public SecureKey getSecureKeyById(String id) {
        return (SecureKey) super.getById(id);
    }

    @Override
    public void deleteAllSecureKeys(final TaskCompleteCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public SecureKey initSecureKey(JSONObject jsonObject) {
        SecureKey secureKey = new SecureKey();
        insert(secureKey, null);
        return secureKey;
    }

    @Override
    BaseDao getModel() {
        return mSecureKeyDao;
    }
}
