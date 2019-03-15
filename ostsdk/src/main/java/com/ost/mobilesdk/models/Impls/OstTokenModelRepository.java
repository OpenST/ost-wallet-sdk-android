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
import com.ost.mobilesdk.database.daos.OstTokenDao;
import com.ost.mobilesdk.models.OstTokenModel;
import com.ost.mobilesdk.models.entities.OstToken;

class OstTokenModelRepository extends OstBaseModelCacheRepository implements OstTokenModel {

    private OstTokenDao mOstTokenDao;

    OstTokenModelRepository() {
        super(5);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTokenDao = db.tokenDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTokenDao;
    }

    @Override
    public OstToken getEntityById(String id) {
        return (OstToken) super.getById(id);
    }

    @Override
    public OstToken[] getEntitiesByParentId(String id) {
        return (OstToken[]) super.getByParentId(id);
    }
}