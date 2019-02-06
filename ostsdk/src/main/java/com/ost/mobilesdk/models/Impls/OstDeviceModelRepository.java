package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstDeviceDao;
import com.ost.mobilesdk.models.OstDeviceModel;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstDevice;

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
    public void insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        super.insert(ostBaseEntity, new OstTaskCallback() {});
    }

    @Override
    public OstDevice getEntityById(String id) {
        return (OstDevice)super.getById(id);
    }

    @Override
    public OstDevice[] getEntitiesByParentId(String id) {
        return (OstDevice[]) super.getByParentId(id);
    }

    @Override
    public void deleteEntity(String id) {
        super.delete(id, new OstTaskCallback() {});
    }

    @Override
    public void deleteAllEntities() {
        super.deleteAll(new OstTaskCallback() {});
    }
}
