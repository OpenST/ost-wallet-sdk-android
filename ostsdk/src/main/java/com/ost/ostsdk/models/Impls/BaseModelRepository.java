package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.utils.DispatchAsync;

import java.util.HashMap;

abstract class BaseModelRepository {

    BaseModelRepository() {
    }

    public void insert(final BaseEntity baseEntity, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insert(baseEntity);
            }

            @Override
            public void onExecuteComplete() {
                if (callback != null) {
                    callback.onTaskComplete();
                }
            }
        }));
    }

    public void insertAll(final BaseEntity[] baseEntities, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insertAll(baseEntities);
            }

            @Override
            public void onExecuteComplete() {
                if (callback != null) {
                    callback.onTaskComplete();
                }
            }
        }));
    }

    public void delete(final BaseEntity baseEntity, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().delete(baseEntity);
            }

            @Override
            public void onExecuteComplete() {
                if (callback != null) {
                    callback.onTaskComplete();
                }
            }
        }));
    }

    public BaseEntity[] getByIds(String[] ids) {
        return getModel().getByIds(ids);
    }


    public BaseEntity getById(String id) {
        return getModel().getById(id);
    }


    public void deleteAll(final TaskCompleteCallback callback) {
        getModel().deleteAll();
    }

    abstract BaseDao getModel();

}