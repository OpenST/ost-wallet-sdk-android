package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.utils.DispatchAsync;

abstract class BaseModelRepository {

    BaseModelRepository() {
    }

    public void insert(final BaseEntity baseEntity, final TaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insert(baseEntity);
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }

    public void insertAll(final BaseEntity[] baseEntities, final TaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insertAll(baseEntities);
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }

    public void delete(final BaseEntity baseEntity, final TaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().delete(baseEntity);
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }

    public BaseEntity[] getByIds(String[] ids) {
        return getModel().getByIds(ids);
    }


    public BaseEntity getById(String id) {
        return getModel().getById(id);
    }


    public void deleteAll(final TaskCallback callback) {
        getModel().deleteAll();
    }

    abstract BaseDao getModel();

}