package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.RuleDao;
import com.ost.ostsdk.models.RuleModel;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.entities.Rule;
import com.ost.ostsdk.utils.DispatchAsync;

public final class RuleModelRepository implements RuleModel {

    private RuleDao mRuleDao;

    private RuleModelRepository() {
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mRuleDao = db.ruleDao();
    }

    @Override
    public void insertUser(final Rule ruleEntity, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                mRuleDao.insertAll(ruleEntity);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
    }

    @Override
    public void insertAllUsers(final Rule[] ruleEntity, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                mRuleDao.insertAll(ruleEntity);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));

    }

    @Override
    public void deleteUser(final Rule ruleEntity, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                mRuleDao.delete(ruleEntity);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
    }

    @Override
    public Rule[] getUsersByIds(String[] ids) {
        return mRuleDao.getByIds(ids);
    }

    @Override
    public Rule getUserById(String id) {
        return mRuleDao.getById(id);
    }

    @Override
    public void deleteAllUsers(final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                mRuleDao.deleteAll();
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
    }
}
