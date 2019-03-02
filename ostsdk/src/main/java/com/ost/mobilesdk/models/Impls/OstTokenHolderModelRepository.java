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