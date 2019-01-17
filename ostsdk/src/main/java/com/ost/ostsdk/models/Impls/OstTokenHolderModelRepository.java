package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstTokenHolderDao;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.OstTokenHolderModel;
import com.ost.ostsdk.models.entities.OstTokenHolder;

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
    public void insertTokenHolder(OstTokenHolder ostTokenHolder) {
        super.insert(ostTokenHolder, new OstTaskCallback() {});
    }

    @Override
    public void insertAllTokenHolders(OstTokenHolder[] ostTokenHolders) {
        super.insertAll(ostTokenHolders, new OstTaskCallback() {});
    }

    @Override
    public void deleteTokenHolder(String id) {
        super.delete(id, new OstTaskCallback() {});
    }

    @Override
    public OstTokenHolder[] getTokenHoldersByIds(String[] ids) {
        return (OstTokenHolder[]) super.getByIds(ids);
    }

    @Override
    public OstTokenHolder getTokenHolderById(String id) {
        return (OstTokenHolder) super.getById(id);
    }

    @Override
    public void deleteAllTokenHolders() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstTokenHolder insert(OstTokenHolder ostTokenHolder) {
        insert(ostTokenHolder, new OstTaskCallback() {});
        return ostTokenHolder;
    }
}