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
import com.ost.walletsdk.database.daos.OstDeviceDao;
import com.ost.walletsdk.models.OstDeviceModel;
import com.ost.walletsdk.models.entities.OstDevice;

import org.web3j.crypto.Keys;

class OstDeviceModelRepository extends OstBaseModelCacheRepository implements OstDeviceModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstDeviceDao mOstDeviceDao;

    OstDeviceModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstDeviceDao = db.multiSigWalletDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstDeviceDao;
    }


    @Override
    public OstDevice getEntityById(String id) {
        return (OstDevice)super.getById(Keys.toChecksumAddress(id));
    }

    @Override
    public OstDevice[] getEntitiesByParentId(String id) {
        return (OstDevice[]) super.getByParentId(id);
    }
}