package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.database.daos.OstSecureKeyDao;
import com.ost.ostsdk.models.OstSecureKeyModel;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstSecureKey;
import com.ost.ostsdk.utils.DispatchAsync;

public class OstSecureKeyModelRepository implements OstSecureKeyModel {

    private OstSecureKeyDao mOstSecureKeyDao;

    public OstSecureKeyModelRepository() {
        super();
        OstSdkKeyDatabase db = OstSdkKeyDatabase.getDatabase();
        mOstSecureKeyDao = db.secureKeyDao();
    }


    @Override
    public void insertSecureKey(final OstSecureKey baseEntity, final OstTaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insert(baseEntity);
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }

    @Override
    public void insertAllSecureKeys(final OstSecureKey[] ostSecureKeys, final OstTaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insertAll(ostSecureKeys);
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }


    public void deleteSecureKey(final String id, final OstTaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().delete(id);
            }

            @Override
            public void onExecuteComplete() {
                callback.onSuccess();
            }
        }));
    }

    @Override
    public OstSecureKey[] getSecureKeysByIds(String[] ids) {
        return new OstSecureKey[0];
    }

    @Override
    public OstSecureKey getSecureKeyById(String id) {
        return null;
    }

    @Override
    public void deleteAllSecureKeys(OstTaskCallback callback) {

    }

    public OstSecureKey[] getByIds(String[] ids) {
        return getModel().getByIds(ids);
    }


    public OstSecureKey getById(String id) {
        return getModel().getById(id);
    }


    public void deleteAll(final OstTaskCallback callback) {
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
