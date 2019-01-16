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


    public void insertUser(final OstUser ostUser) {
        super.insert(ostUser, new OstTaskCallback() {});
    }

    @Override
    public void insertAllUsers(final OstUser[] ostUser) {
        super.insertAll(ostUser, new OstTaskCallback() {});
    }

    @Override
    public void deleteUser(String id) {
        super.delete(id, new OstTaskCallback() {});
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
    public void deleteAllUsers() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstUser insert(OstUser ostUser) {
        insert(ostUser, new OstTaskCallback() {});
        return ostUser;
    }

    @Override
    public OstUser update(OstUser ostUser) {
        insertUser(ostUser);
        return ostUser;
    }

    @Override
    OstBaseDao getModel() {
        return mOstUserDao;
    }


}
