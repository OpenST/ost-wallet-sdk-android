package com.ost.ostsdk.models.Impls;

import android.content.Context;
import android.os.AsyncTask;

import com.ost.ostsdk.Utils.DispatchAsync;
import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.UserDao;
import com.ost.ostsdk.models.entities.User;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.UserModel;

import org.json.JSONObject;

public class UserModelRepository implements UserModel {

    private UserDao mUserDao;

    public UserModelRepository(Context application) {
        OstSdkDatabase db = OstSdkDatabase.getDatabase(application);
        mUserDao = db.userDao();
    }

    @Override
    public void insertUser(final User user, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                mUserDao.insertAll(user);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
    }

    @Override
    public void insertAllUsers(final User[] user, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                mUserDao.insertAll(user);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
    }

    @Override
    public void deleteUser(final User user, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                mUserDao.delete(user);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
    }

    @Override
    public User getUsersByIds(double[] ids) {
        return mUserDao.getByIds(ids);
    }

    @Override
    public User getUserById(double id) {
        return mUserDao.getById(id);
    }

    @Override
    public void deleteAllUsers(final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                mUserDao.deleteAll();
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
    }

    @Override
    public User initUser(JSONObject jsonObject) {
        //Code to update user in cache and db.
        return new User(jsonObject);
    }
}
