/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstTokenHolderDao;
import com.ost.mobilesdk.models.OstTokenHolderModel;
import com.ost.mobilesdk.models.entities.OstTokenHolder;

import org.web3j.crypto.Keys;

class OstTokenHolderModelRepository extends OstBaseModelCacheRepository implements OstTokenHolderModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstTokenHolderDao mOstTokenHolderDao;

    OstTokenHolderModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTokenHolderDao = db.tokenHolderDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTokenHolderDao;
    }

    @Override
    public OstTokenHolder getEntityById(String id) {
        return (OstTokenHolder)super.getById(Keys.toChecksumAddress(id));
    }

    @Override
    public OstTokenHolder[] getEntitiesByParentId(String id) {
        return (OstTokenHolder[]) super.getByParentId(id);
    }
}