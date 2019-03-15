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
import com.ost.mobilesdk.database.daos.OstUserDao;
import com.ost.mobilesdk.models.OstUserModel;
import com.ost.mobilesdk.models.entities.OstUser;

class OstUserModelRepository extends OstBaseModelCacheRepository implements OstUserModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstUserDao mOstUserDao;

    OstUserModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstUserDao = db.userDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstUserDao;
    }

    

    @Override
    public OstUser getEntityById(String id) {
        return (OstUser) super.getById(id);
    }

    @Override
    public OstUser[] getEntitiesByParentId(String id) {
        return (OstUser[]) super.getByParentId(id);
    }
}
