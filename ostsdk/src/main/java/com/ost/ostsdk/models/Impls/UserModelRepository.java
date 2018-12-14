package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.UserDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.UserModel;
import com.ost.ostsdk.models.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

class UserModelRepository extends BaseModelCacheRepository implements UserModel {

    private static final int LRU_CACHE_SIZE = 5;
    private UserDao mUserDao;

    UserModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mUserDao = db.userDao();
    }


    public void insertUser(final User user, final TaskCallback callback) {
        super.insert(user, callback);
    }

    @Override
    public void insertAllUsers(final User[] user, final TaskCallback callback) {
        super.insertAll(user, callback);
    }

    @Override
    public void deleteUser(final User user, final TaskCallback callback) {
        super.delete(user, callback);
    }

    @Override
    public User[] getUsersByIds(String[] ids) {
        return (User[]) super.getByIds(ids);
    }

    @Override
    public User getUserById(String id) {
        return (User) super.getById(id);
    }

    @Override
    public void deleteAllUsers(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public User initUser(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        User user = new User(jsonObject);
        insert(user, callback);
        return user;
    }

    @Override
    public User initUser(JSONObject jsonObject) throws JSONException {
        return initUser(jsonObject, new TaskCallback() {
        });
    }

    @Override
    BaseDao getModel() {
        return mUserDao;
    }

}
