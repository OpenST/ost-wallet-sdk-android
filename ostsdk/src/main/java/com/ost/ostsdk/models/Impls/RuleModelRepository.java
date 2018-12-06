package com.ost.ostsdk.models.Impls;

import android.content.Context;
import android.os.AsyncTask;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.RuleDao;
import com.ost.ostsdk.models.entities.Rule;
import com.ost.ostsdk.models.DbProcessCallback;
import com.ost.ostsdk.models.RuleModel;

public class RuleModelRepository implements RuleModel {

    private RuleDao mRuleDao;

    public RuleModelRepository(Context application) {
        OstSdkDatabase db = OstSdkDatabase.getDatabase(application);
        mRuleDao = db.ruleDao();
    }

    @Override
    public void insertUser(Rule userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(Rule... params) {
                mRuleDao.insertAll(params);
            }
        }, callback).execute(userEntity);
    }

    @Override
    public void insertAllUsers(Rule[] userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(Rule... params) {
                mRuleDao.insertAll(params);
            }
        }, callback).execute(userEntity);
    }

    @Override
    public void deleteUser(Rule userEntity, DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(Rule... params) {
                mRuleDao.delete(params[0]);
            }
        }, callback).execute(userEntity);

    }

    @Override
    public Rule getUsersByIds(double[] ids) {
        return mRuleDao.getByIds(ids);
    }

    @Override
    public Rule getUserById(double id) {
        return mRuleDao.getById(id);
    }

    @Override
    public void deleteAllUsers(DbProcessCallback callback) {
        new DataBaseAsyncTask(new Executor() {
            @Override
            public void execute(Rule... params) {
                mRuleDao.deleteAll();
            }
        }, callback).execute();
    }

    private interface Executor {
        void execute(final Rule... params);
    }

    private static class DataBaseAsyncTask extends AsyncTask<Rule, Void, Void> {

        private Executor mExecutor;
        private DbProcessCallback mCallback;

        DataBaseAsyncTask(Executor executor, DbProcessCallback callback) {
            mExecutor = executor;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(final Rule... params) {
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
