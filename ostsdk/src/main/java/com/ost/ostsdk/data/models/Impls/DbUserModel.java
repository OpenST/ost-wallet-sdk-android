package com.ost.ostsdk.data.models.Impls;

import android.content.Context;
import android.os.AsyncTask;

import com.ost.ostsdk.data.database.OstSdkDatabase;
import com.ost.ostsdk.data.database.daos.UserDao;
import com.ost.ostsdk.data.models.entities.UserEntity;
import com.ost.ostsdk.data.models.DbProcessCallback;
import com.ost.ostsdk.data.models.UserModel;

public class DbUserModel implements UserModel {

    private UserDao mUserDao;

    public DbUserModel(Context application) {
        OstSdkDatabase db = OstSdkDatabase.getDatabase(application);
        mUserDao = db.userDao();
    }

    @Override
    public void insertUser(UserEntity userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(UserEntity... params) {
                mUserDao.insertAll(params);
            }
        }, callback).execute(userEntity);
    }

    @Override
    public void insertAllUsers(UserEntity[] userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(UserEntity... params) {
                mUserDao.insertAll(params);
            }
        }, callback).execute(userEntity);
    }

    @Override
    public void deleteUser(UserEntity userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(UserEntity... params) {
                mUserDao.delete(params[0]);
            }
        }, callback).execute(userEntity);

    }

    @Override
    public UserEntity getUsersByIds(double[] ids) {
        return mUserDao.getByIds(ids);
    }

    @Override
    public UserEntity getUserById(double id) {
        return mUserDao.getById(id);
    }

    @Override
    public void deleteAllUsers(DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(UserEntity... params) {
                mUserDao.deleteAll();
            }
        }, callback).execute();
    }

    private interface Executor {
        void execute(final UserEntity... params);
    }

    private static class DataBaseAsyncTask extends AsyncTask<UserEntity, Void, Void> {

        private Executor mExecutor;
        private DbProcessCallback mCallback;

        DataBaseAsyncTask(Executor executor, DbProcessCallback callback) {
            mExecutor = executor;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(final UserEntity... params) {
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
