package com.ost.ostsdk.models.Impls;

import android.util.Log;

import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.utils.DispatchAsync;

import org.json.JSONObject;

abstract class BaseModelRepository {

    private static final String TAG = "BaseModelRepository";

    BaseModelRepository() {
    }

    public void insert(final OstBaseEntity baseEntity, final TaskCallback callback) {
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

    public void insertAll(final OstBaseEntity[] baseEntities, final TaskCallback callback) {
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

    public OstBaseEntity[] getByIds(String[] ids) {
        return processEntity(getModel().getByIds(ids));
    }


    public OstBaseEntity getById(String id) {
        OstBaseEntity baseEntity = getModel().getById(id);
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

    abstract OstBaseDao getModel();

    protected OstBaseEntity[] getByParentId(String id) {
        return processEntity(getModel().getByParentId(id));
    }

    private OstBaseEntity[] processEntity(OstBaseEntity[] baseEntities) {
        for (OstBaseEntity baseEntity : baseEntities) {
            try {
                baseEntity.processJson(new JSONObject(baseEntity.getData()));
            } catch (Exception exception) {
                Log.e(TAG, "Exception in BaseModelRepository for parsing data");
            }
        }
        return baseEntities;
    }
}