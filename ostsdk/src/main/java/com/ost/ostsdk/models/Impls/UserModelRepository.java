package com.ost.ostsdk.models.Impls;

import android.content.Context;
import android.os.AsyncTask;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.UserDao;
import com.ost.ostsdk.models.entities.User;
import com.ost.ostsdk.models.DbProcessCallback;
import com.ost.ostsdk.models.UserModel;

import org.json.JSONObject;

public class UserModelRepository implements UserModel {

    private UserDao mUserDao;

    public UserModelRepository(Context application) {
        OstSdkDatabase db = OstSdkDatabase.getDatabase(application);
        mUserDao = db.userDao();
    }

    @Override
    public void insertUser(User user, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(User... params) {
                mUserDao.insertAll(params);
            }
        }, callback).execute(user);
    }

    @Override
    public void insertAllUsers(User[] user, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(User... params) {
                mUserDao.insertAll(params);
            }
        }, callback).execute(user);
    }

    @Override
    public void deleteUser(User user, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(User... params) {
                mUserDao.delete(params[0]);
            }
        }, callback).execute(user);

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
    public void deleteAllUsers(DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(User... params) {
                mUserDao.deleteAll();
            }
        }, callback).execute();
    }

    @Override
    public User initUser(JSONObject jsonObject) {
        //Code to update user in cache and db.
        return new User(jsonObject);
    }

    private interface Executor {
        void execute(final User... params);
    }

    private static class DataBaseAsyncTask extends AsyncTask<User, Void, Void> {

        private Executor mExecutor;
        private DbProcessCallback mCallback;

        DataBaseAsyncTask(Executor executor, DbProcessCallback callback) {
            mExecutor = executor;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(final User... params) {
            mExecutor.execute(params);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (null != mCallback) {
                mCallback.onProcessComplete();
            }
            super.onPostExecute(aVoid);
        }
    }
}
