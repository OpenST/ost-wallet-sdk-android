package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstTokenDao;
import com.ost.ostsdk.models.OstCreditsModel;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstCredits;

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
    public OstCredits insert(OstCredits ostCredits) {
        super.insert(ostCredits, new OstTaskCallback() {});
        return ostCredits;
    }
}