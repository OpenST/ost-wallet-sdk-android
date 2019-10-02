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
import com.ost.walletsdk.database.daos.OstTransactionDao;
import com.ost.walletsdk.models.OstTransactionModel;
import com.ost.walletsdk.models.entities.OstTransaction;

class OstTransactionModelRepository extends OstBaseModelCacheRepository implements OstTransactionModel {

    private static final int LRU_CACHE_SIZE = 150;
    private OstTransactionDao mOstTransactionDao;

    OstTransactionModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTransactionDao = db.executableRuleDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTransactionDao;
    }

    @Override
    public OstTransaction getEntityById(String id) {
        return (OstTransaction)super.getById(id);
    }

    @Override
    public OstTransaction[] getEntitiesByParentId(String id) {
        return (OstTransaction[]) super.getByParentId(id);
    }
}
