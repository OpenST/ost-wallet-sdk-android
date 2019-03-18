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

import com.ost.walletsdk.database.OstSdkKeyDatabase;
import com.ost.walletsdk.database.daos.OstSecureKeyDao;
import com.ost.walletsdk.models.OstSecureKeyModel;
import com.ost.walletsdk.models.entities.OstSecureKey;
import com.ost.walletsdk.utils.AsyncStatus;
import com.ost.walletsdk.utils.DispatchAsync;

import java.util.concurrent.Future;

public class OstSecureKeyModelRepository implements OstSecureKeyModel {

    private OstSecureKeyDao mOstSecureKeyDao;

    public OstSecureKeyModelRepository() {
        super();
        OstSdkKeyDatabase db = OstSdkKeyDatabase.getDatabase();
        mOstSecureKeyDao = db.secureKeyDao();
    }

    @Override
    public Future<AsyncStatus> insertSecureKey(OstSecureKey ostSecureKey) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                getModel().insert(ostSecureKey);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstSecureKey getByKey(String id) {
        return getModel().getById(id);
    }

    public Future<AsyncStatus> delete(String id) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                getModel().delete(id);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public Future<AsyncStatus> deleteAllSecureKeys() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                getModel().deleteAll();
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstSecureKey initSecureKey(String key, byte[] data) {
        OstSecureKey ostSecureKey = new OstSecureKey(key, data);
        insertSecureKey(ostSecureKey);
        return ostSecureKey;
    }

    OstSecureKeyDao getModel() {
        return mOstSecureKeyDao;
    }
}