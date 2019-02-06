package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstBaseDao;
import com.ost.mobilesdk.database.daos.OstTokenDao;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.models.OstTokenModel;
import com.ost.mobilesdk.models.entities.OstBaseEntity;
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
    public void insertOrUpdateEntity(OstBaseEntity ostBaseEntity) {
        super.insert(ostBaseEntity, new OstTaskCallback() {});
    }

    @Override
    public OstToken getEntityById(String id) {
        return (OstToken) super.getById(id);
    }

    @Override
    public OstToken[] getEntitiesByParentId(String id) {
        return (OstToken[]) super.getByParentId(id);
    }

    @Override
    public void deleteEntity(String id) {
        super.delete(id,  new OstTaskCallback() {});
    }

    @Override
    public void deleteAllEntities() {
        super.deleteAll( new OstTaskCallback() {});
    }
}