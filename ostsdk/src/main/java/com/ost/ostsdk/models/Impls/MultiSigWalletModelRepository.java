package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.MultiSigWalletDao;
import com.ost.ostsdk.models.MultiSigWalletModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.MultiSigWallet;

import org.json.JSONException;
import org.json.JSONObject;

class MultiSigWalletModelRepository extends BaseModelCacheRepository implements MultiSigWalletModel {

    private static final int LRU_CACHE_SIZE = 5;
    private MultiSigWalletDao mMultiSigWalletDao;

    MultiSigWalletModelRepository() {
        super(LRU_CACHE_SIZE);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mMultiSigWalletDao = db.multiSigWalletDao();
    }


    @Override
    public void insertMultiSigWallet(final MultiSigWallet multiSigWallet, final TaskCallback callback) {
        super.insert(multiSigWallet, callback);
    }

    @Override
    public void insertAllMultiSigWallets(final MultiSigWallet[] multiSigWallet, final TaskCallback callback) {
        super.insertAll(multiSigWallet, callback);
    }

    @Override
    public void deleteMultiSigWallet(final String id, final TaskCallback callback) {
        super.delete(id, callback);
    }

    @Override
    public MultiSigWallet[] getMultiSigWalletsByIds(String[] ids) {
        return (MultiSigWallet[]) super.getByIds(ids);
    }

    @Override
    public MultiSigWallet getMultiSigWalletById(String id) {
        return (MultiSigWallet) super.getById(id);
    }

    @Override
    public void deleteAllMultiSigWallets(final TaskCallback callback) {
        super.deleteAll(callback);
    }

    @Override
    public MultiSigWallet initMultiSigWallet(JSONObject jsonObject) throws JSONException {
        MultiSigWallet multiSigWallet = new MultiSigWallet(jsonObject);
        insert(multiSigWallet, null);
        return multiSigWallet;
    }

    @Override
    BaseDao getModel() {
        return mMultiSigWalletDao;
    }
}
