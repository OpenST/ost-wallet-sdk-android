package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstDeviceManagerDao;
import com.ost.mobilesdk.models.OstDeviceManagerModel;
import com.ost.mobilesdk.models.entities.OstDeviceManager;

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
