package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstUserDao;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.OstUserModel;
import com.ost.ostsdk.models.entities.OstUser;

import org.json.JSONException;
import org.json.JSONObject;

class OstUserModelRepository extends OstBaseModelCacheRepository implements OstUserModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstUserDao mOstUserDao;

    OstUserModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstUserDao = db.userDao();
    }


    public void insertUser(final OstUser ostUser, final OstTaskCallback callback) {
        super.insert(ostUser, callback);
    }

    @Override
    public void insertAllUsers(final OstUser[] ostUser, final OstTaskCallback callback) {
        super.insertAll(ostUser, callback);
    }

    @Override
    public void deleteUser(String id, OstTaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public OstUser[] getUsersByIds(String[] ids) {
        return (OstUser[]) super.getByIds(ids);
    }

    @Override
    public OstUser getUserById(String id) {
        return (OstUser) super.getById(id);
    }

    @Override
    public void deleteAllUsers(final OstTaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public OstUser initUser(JSONObject jsonObject, OstTaskCallback callback) throws JSONException {
        OstUser ostUser = new OstUser(jsonObject);
        insert(ostUser, callback);
        return ostUser;
    }

    @Override
    public OstUser initUser(JSONObject jsonObject) throws JSONException {
        return initUser(jsonObject, new OstTaskCallback() {
        });
    }

    @Override
    public OstUser update(OstUser ostUser, OstTaskCallback callback) {
        ostUser.setUts(System.currentTimeMillis());
        ostUser.updateJSON();
        insertUser(ostUser, callback);
        return ostUser;
    }

    @Override
    OstBaseDao getModel() {
        return mOstUserDao;
    }


}
