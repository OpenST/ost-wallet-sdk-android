package com.ost.mobilesdk.models.Impls;

import android.util.Log;

import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

abstract class OstBaseModelRepository {

    private static final String TAG = "OstBaseModelRepository";

    OstBaseModelRepository() {
    }

    public void insert(final OstBaseEntity baseEntity) {
        getModel().insert(baseEntity);
    }

    public void insertAll(final OstBaseEntity[] baseEntities) {
        getModel().insertAll(baseEntities);
    }

    public void delete(final String id) {
        getModel().delete(id);
    }

    public OstBaseEntity[] getByIds(String[] ids) {
        return processEntity(getModel().getByIds(ids));
    }


    public OstBaseEntity getById(String id) {
        OstBaseEntity baseEntity = getModel().getById(id);
        if (null != baseEntity) {
            try {
                baseEntity.processJson(baseEntity.getData());
            } catch (Exception exception) {
                Log.e(TAG, "Exception in OstBaseModelRepository::getById for parsing data");
            }
        }
        return baseEntity;
    }

    public void deleteAll() {
        getModel().deleteAll();
    }

    abstract OstBaseDao getModel();

    protected OstBaseEntity[] getByParentId(String id) {
        return processEntity(getModel().getByParentId(id));
    }

    private OstBaseEntity[] processEntity(OstBaseEntity[] baseEntities) {
        for (OstBaseEntity baseEntity : baseEntities) {
            try {
                baseEntity.processJson(baseEntity.getData());
            } catch (Exception exception) {
                Log.e(TAG, "Exception in OstBaseModelRepository:: processEntity for parsing data");
            }
        }
        return baseEntities;
    }

    public Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstBaseModelRepository.this.insert(ostBaseEntity);
                return new AsyncStatus(true);
            }
        });
    }

}