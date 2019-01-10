package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkKeyDatabase;
import com.ost.ostsdk.database.daos.SecureKeyDao;
import com.ost.ostsdk.models.SecureKeyModel;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.SecureKey;
import com.ost.ostsdk.utils.DispatchAsync;

public class SecureKeyModelRepository implements SecureKeyModel {

    private SecureKeyDao mSecureKeyDao;

    public SecureKeyModelRepository() {
        super();
        OstSdkKeyDatabase db = OstSdkKeyDatabase.getDatabase();
        mSecureKeyDao = db.secureKeyDao();
    }


    @Override
    public void insertSecureKey(final SecureKey baseEntity, final TaskCallback callback) {
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
    public void insertAllSecureKeys(final SecureKey[] secureKeys, final TaskCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insertAll(secureKeys);
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
    public SecureKey[] getSecureKeysByIds(String[] ids) {
        return new SecureKey[0];
    }

    @Override
    public SecureKey getSecureKeyById(String id) {
        return null;
    }

    @Override
    public void deleteAllSecureKeys(TaskCallback callback) {

    }

    public SecureKey[] getByIds(String[] ids) {
        return getModel().getByIds(ids);
    }


    public SecureKey getById(String id) {
        return getModel().getById(id);
    }


    public void deleteAll(final TaskCallback callback) {
        getModel().deleteAll();
    }

    public SecureKey initSecureKey(String key, byte[] data) {
        SecureKey secureKey = new SecureKey(key, data);
        insertSecureKey(secureKey, new TaskCallback() {
        });
        return secureKey;
    }

    SecureKeyDao getModel() {
        return mSecureKeyDao;
    }
}
