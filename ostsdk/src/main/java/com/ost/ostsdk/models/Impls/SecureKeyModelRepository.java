package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.database.daos.SecureKeyDao;
import com.ost.ostsdk.models.SecureKeyModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.OstSecureKey;
import com.ost.ostsdk.utils.DispatchAsync;

public class SecureKeyModelRepository implements SecureKeyModel {

    private SecureKeyDao mSecureKeyDao;

    public SecureKeyModelRepository() {
        super();
        OstSdkKeyDatabase db = OstSdkKeyDatabase.getDatabase();
        mSecureKeyDao = db.secureKeyDao();
    }


    @Override
    public void insertSecureKey(final OstSecureKey baseEntity, final TaskCallback callback) {
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
    public void insertAllSecureKeys(final OstSecureKey[] ostSecureKeys, final TaskCallback callback) {
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


    public void deleteSecureKey(final String id, final TaskCallback callback) {
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
    public void deleteAllSecureKeys(TaskCallback callback) {

    }

    public OstSecureKey[] getByIds(String[] ids) {
        return getModel().getByIds(ids);
    }


    public OstSecureKey getById(String id) {
        return getModel().getById(id);
    }


    public void deleteAll(final TaskCallback callback) {
        getModel().deleteAll();
    }

    public OstSecureKey initSecureKey(String key, byte[] data) {
        OstSecureKey ostSecureKey = new OstSecureKey(key, data);
        insertSecureKey(ostSecureKey, new TaskCallback() {
        });
        return ostSecureKey;
    }

    SecureKeyDao getModel() {
        return mSecureKeyDao;
    }
}
