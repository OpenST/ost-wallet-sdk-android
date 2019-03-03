package com.ost.mobilesdk.models.Impls;

import com.ost.mobilesdk.database.OstSdkDatabase;
import com.ost.mobilesdk.database.daos.OstSessionKeyDao;
import com.ost.mobilesdk.models.OstSessionKeyModel;
import com.ost.mobilesdk.models.entities.OstSessionKey;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.DispatchAsync;

import org.web3j.crypto.Keys;

import java.util.concurrent.Future;

public class OstSessionKeyModelRepository implements OstSessionKeyModel {

    private OstSessionKeyDao mOstSessionKeyDao;

    public OstSessionKeyModelRepository() {
        super();
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstSessionKeyDao = db.sessionKeyDao();
    }

    @Override
    public Future<AsyncStatus> insertSessionKey(OstSessionKey ostSessionKey) {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                getModel().insert(ostSessionKey);
                return new AsyncStatus(true);
            }
        });
    }

    private OstSessionKeyDao getModel() {
        return mOstSessionKeyDao;
    }

    @Override
    public OstSessionKey getByKey(String key) {
        return getModel().getById(Keys.toChecksumAddress(key));
    }

    @Override
    public Future<AsyncStatus> deleteAllSessionKeys() {
        return DispatchAsync.dispatch(new DispatchAsync.Executor() {
            @Override
            public AsyncStatus call() {
                getModel().deleteAll();
                return new AsyncStatus(true);
            }
        });
    }

    @Override
    public OstSessionKey initSessionKey(String key, byte[] data) {
        OstSessionKey ostSessionKey = new OstSessionKey(key, data);
        insertSessionKey(ostSessionKey);
        return ostSessionKey;
    }
}