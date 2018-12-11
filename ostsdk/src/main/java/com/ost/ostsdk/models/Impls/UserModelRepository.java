package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.UserDao;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.UserModel;
import com.ost.ostsdk.models.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

class UserModelRepository extends BaseModelRepository implements UserModel {

    private static final int LRU_CACHE_SIZE = 5;
    private UserDao mUserDao;

    UserModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mUserDao = db.userDao();
    }


    @Override
    public void insertUser(final User user, final TaskCompleteCallback callback) {
        super.insert(user, callback);
    }

    @Override
    public void insertAllUsers(final User[] user, final TaskCompleteCallback callback) {
        super.insertAll(user, callback);
    }

    @Override
    public void deleteUser(final User user, final TaskCompleteCallback callback) {
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
    public void deleteAllUsers(final TaskCompleteCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public User initUser(JSONObject jsonObject) throws JSONException {
        User user = new User(jsonObject);
        insert(user, null);
        return user;
    }

    @Override
    BaseDao getModel() {
        return mUserDao;
    }
}
