package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstTokenDao;
import com.ost.mobilesdk.models.OstCreditsModel;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstCredits;

class OstCreditsModelRepository extends OstBaseModelCacheRepository implements OstCreditsModel {

    private OstTokenDao mOstTokenDao;

    OstCreditsModelRepository() {
        super(5);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTokenDao = db.tokenDao();
    }

    @Override
    OstBaseDao getModel() {
        return mOstTokenDao;
    }

    @Override
    public void insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        super.insert(ostBaseEntity, new OstTaskCallback() {});
    }

    @Override
    public OstCredits getEntityById(String id) {
        return (OstCredits) super.getById(id);
    }

    @Override
    public OstCredits[] getEntitiesByParentId(String id) {
        return (OstCredits[]) super.getByParentId(id);
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