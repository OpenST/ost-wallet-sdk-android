package com.ost.ostsdk.data.models.Impls;

import android.content.Context;
import android.os.AsyncTask;

import com.ost.ostsdk.data.database.OstSdkDatabase;
import com.ost.ostsdk.data.database.daos.RuleDao;
import com.ost.ostsdk.data.models.entities.RuleEntity;
import com.ost.ostsdk.data.models.DbProcessCallback;
import com.ost.ostsdk.data.models.RuleModel;

public class DbRuleModel implements RuleModel {

    private RuleDao mRuleDao;

    public DbRuleModel(Context application) {
        OstSdkDatabase db = OstSdkDatabase.getDatabase(application);
        mRuleDao = db.ruleDao();
    }

    @Override
    public void insertUser(RuleEntity userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(RuleEntity... params) {
                mRuleDao.insertAll(params);
            }
        }, callback).execute(userEntity);
    }

    @Override
    public void insertAllUsers(RuleEntity[] userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(RuleEntity... params) {
                mRuleDao.insertAll(params);
            }
        }, callback).execute(userEntity);
    }

    @Override
    public void deleteUser(RuleEntity userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(RuleEntity... params) {
                mRuleDao.delete(params[0]);
            }
        }, callback).execute(userEntity);

    }

    @Override
    public RuleEntity getUsersByIds(double[] ids) {
        return mRuleDao.getByIds(ids);
    }

    @Override
    public RuleEntity getUserById(double id) {
        return mRuleDao.getById(id);
    }

    @Override
    public void deleteAllUsers(DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(RuleEntity... params) {
                mRuleDao.deleteAll();
            }
        }, callback).execute();
    }

    private interface Executor {
        void execute(final RuleEntity... params);
    }

    private static class DataBaseAsyncTask extends AsyncTask<RuleEntity, Void, Void> {

        private Executor mExecutor;
        private DbProcessCallback mCallback;

        DataBaseAsyncTask(Executor executor, DbProcessCallback callback) {
            mExecutor = executor;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(final RuleEntity... params) {
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
