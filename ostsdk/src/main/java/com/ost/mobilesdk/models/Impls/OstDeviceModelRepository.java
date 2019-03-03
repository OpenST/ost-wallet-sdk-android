package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstDeviceDao;
import com.ost.mobilesdk.models.OstDeviceModel;
import com.ost.mobilesdk.models.entities.OstDevice;

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