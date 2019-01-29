package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.OstSessionKeyDao;
import com.ost.ostsdk.models.OstSessionKeyModel;
import com.ost.ostsdk.models.entities.OstSessionKey;
import com.ost.ostsdk.utils.DispatchAsync;

public class OstSessionKeyModelRepository implements OstSessionKeyModel {

    private OstSessionKeyDao mOstSessionKeyDao;

    public OstSessionKeyModelRepository() {
        super();
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mOstSessionKeyDao = db.sessionKeyDao();
    }

    @Override
    public void insertSessionKey(OstSessionKey ostSessionKey) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insert(ostSessionKey);
            }
        }));
    }

    private OstSessionKeyDao getModel() {
        return mOstSessionKeyDao;
    }

    @Override
    public OstSessionKey getByKey(String key) {
        return getModel().getById(key);
    }

    @Override
    public void deleteAllSessionKeys() {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().deleteAll();
            }
        }));
    }

    @Override
    public OstSessionKey initSessionKey(String key, byte[] data) {
        OstSessionKey ostSessionKey = new OstSessionKey(key, data);
        insertSessionKey(ostSessionKey);
        return ostSessionKey;
    }
}