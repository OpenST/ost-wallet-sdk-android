package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkKeyDatabase;
import com.ost.mobilesdk.database.daos.OstSecureKeyDao;
import com.ost.mobilesdk.models.OstSecureKeyModel;
import com.ost.mobilesdk.models.entities.OstSecureKey;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import java.util.concurrent.Future;

public class OstSecureKeyModelRepository implements OstSecureKeyModel {

    private OstSecureKeyDao mOstSecureKeyDao;

    public OstSecureKeyModelRepository() {
        super();
        OstSdkKeyDatabase db = OstSdkKeyDatabase.getDatabase();
        mOstSecureKeyDao = db.secureKeyDao();
    }

    @Override
    public Future<AsyncStatus> insertSecureKey(OstSecureKey ostSecureKey) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                getModel().insert(ostSecureKey);
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstSecureKey getByKey(String id) {
        return getModel().getById(id);
    }

    @Override
    public Future<AsyncStatus> deleteAllSecureKeys() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                getModel().deleteAll();
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstSecureKey initSecureKey(String key, byte[] data) {
        OstSecureKey ostSecureKey = new OstSecureKey(key, data);
        insertSecureKey(ostSecureKey);
        return ostSecureKey;
    }

    OstSecureKeyDao getModel() {
        return mOstSecureKeyDao;
    }
}