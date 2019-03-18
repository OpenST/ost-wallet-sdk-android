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

import com.ost.walletsdk.database.OstSdkDatabase;
import com.ost.walletsdk.database.daos.OstBaseDao;
import com.ost.walletsdk.database.daos.OstDeviceManagerDao;
import com.ost.walletsdk.models.OstDeviceManagerModel;
import com.ost.walletsdk.models.entities.OstDeviceManager;

import org.web3j.crypto.Keys;

class OstDeviceManagerModelRepository extends OstBaseModelCacheRepository implements OstDeviceManagerModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceManagerDao mOstDeviceManagerDao;

    OstDeviceManagerModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstDeviceManagerDao = db.multiSigDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstDeviceManagerDao;
    }

    @Override
    public OstDeviceManager getEntityById(String id) {
        return (OstDeviceManager)super.getById(Keys.toChecksumAddress(id));
    }

    @Override
    public OstDeviceManager[] getEntitiesByParentId(String id) {
        return (OstDeviceManager[]) super.getByParentId(id);
    }
}
