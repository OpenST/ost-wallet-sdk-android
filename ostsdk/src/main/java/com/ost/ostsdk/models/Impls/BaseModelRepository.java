package com.ost.ostsdk.models.Impls;

import android.util.Log;

import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.utils.DispatchAsync;

import org.json.JSONObject;

abstract class BaseModelRepository {

    private static final String TAG = "BaseModelRepository";

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

    public void delete(final String id, final TaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().delete(id);
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }

    public BaseEntity[] getByIds(String[] ids) {
        return processEntity(getModel().getByIds(ids));
    }


    public BaseEntity getById(String id) {
        BaseEntity baseEntity = getModel().getById(id);
        try {
            baseEntity.processJson(new JSONObject(baseEntity.getData()));
        } catch (Exception exception) {
            Log.e(TAG, "Exception in BaseModelRepository for parsing data");
        }
        return baseEntity;
    }


    public void deleteAll(final TaskCallback callback) {
        getModel().deleteAll();
    }

    abstract BaseDao getModel();

    protected BaseEntity[] getByParentId(String id) {
        return processEntity(getModel().getByParentId(id));
    }

    private BaseEntity[] processEntity(BaseEntity[] baseEntities) {
        for (BaseEntity baseEntity : baseEntities) {
            try {
                baseEntity.processJson(new JSONObject(baseEntity.getData()));
            } catch (Exception exception) {
                Log.e(TAG, "Exception in BaseModelRepository for parsing data");
            }
        }
        return baseEntities;
    }
}