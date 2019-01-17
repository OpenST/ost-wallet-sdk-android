package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstBaseDao;
import com.ost.ostsdk.database.daos.OstTransactionDao;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.OstTransactionModel;
import com.ost.ostsdk.models.entities.OstTransaction;

class OstTransactionModelRepository extends OstBaseModelCacheRepository implements OstTransactionModel {

    private static final int LRU_CACHE_SIZE = 5;
    private OstTransactionDao mOstTransactionDao;

    OstTransactionModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstTransactionDao = db.executableRuleDao();
    }


    @Override
    public void insertTransaction(final OstTransaction executableRule) {
        super.insert(executableRule, new OstTaskCallback() {});
    }

    @Override
    public void insertAllTransactions(final OstTransaction[] executableRule) {
        super.insertAll(executableRule, new OstTaskCallback() {});
    }

    @Override
    public void deleteTransaction(final String id) {
        super.delete(id, new OstTaskCallback() {});
    }

    @Override
    public OstTransaction[] getTransactionsByIds(String[] ids) {
        return (OstTransaction[]) super.getByIds(ids);
    }

    @Override
    public OstTransaction getTransactionById(String id) {
        return (OstTransaction) super.getById(id);
    }

    @Override
    public void deleteAllTransactions() {
        super.deleteAll(new OstTaskCallback() {});
    }

    @Override
    public OstTransaction insert(OstTransaction ostTransaction) {
        insert(ostTransaction, null);
        return ostTransaction;
    }

    @Override
    OstBaseDao getModel() {
        return mOstTransactionDao;
    }
}
