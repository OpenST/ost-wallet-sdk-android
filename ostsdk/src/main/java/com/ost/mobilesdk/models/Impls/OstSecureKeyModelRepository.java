package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkKeyDatabase;
import com.ost.mobilesdk.database.daos.OstSecureKeyDao;
import com.ost.mobilesdk.models.OstSecureKeyModel;
import com.ost.mobilesdk.models.OstTaskCallback;
import com.ost.mobilesdk.models.entities.OstSecureKey;
import com.ost.mobilesdk.utils.DispatchAsync;

public class OstSecureKeyModelRepository implements OstSecureKeyModel {

    private OstSecureKeyDao mOstSecureKeyDao;

    public OstSecureKeyModelRepository() {
        super();
        OstSdkKeyDatabase db = OstSdkKeyDatabase.getDatabase();
        mOstSecureKeyDao = db.secureKeyDao();
    }

    @Override
    public void insertSecureKey(OstSecureKey ostSecureKey, OstTaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insert(ostSecureKey);
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }

    @Override
    public OstSecureKey getByKey(String id) {
        return getModel().getById(id);
    }

    @Override
    public void deleteAllSecureKeys(final OstTaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().deleteAll();
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }

    @Override
    public OstSecureKey initSecureKey(String key, byte[] data) {
        OstSecureKey ostSecureKey = new OstSecureKey(key, data);
        insertSecureKey(ostSecureKey, new OstTaskCallback() {
        });
        return ostSecureKey;
    }

    OstSecureKeyDao getModel() {
        return mOstSecureKeyDao;
    }
}