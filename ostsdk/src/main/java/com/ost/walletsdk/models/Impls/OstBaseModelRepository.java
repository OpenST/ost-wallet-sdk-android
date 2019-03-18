/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.models.Impls;

import android.util.Log;

import com.ost.walletsdk.database.daos.OstBaseDao;
import com.ost.walletsdk.models.entities.OstBaseEntity;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.DispatchAsync;

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


    public Future<AsyncStatus> deleteEntity(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstBaseModelRepository.this.delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    public Future<AsyncStatus> deleteAllEntities() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                OstBaseModelRepository.this.deleteAll();
                return new AsyncStatus(true);
            }
        });
    }

}