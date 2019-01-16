package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.database.OstSdkDatabase;
import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.database.daos.TokenDao;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.TokenModel;
import com.ost.ostsdk.models.entities.OstToken;

import org.json.JSONException;
import org.json.JSONObject;

class TokenModelRepository extends BaseModelCacheRepository implements TokenModel {

    private TokenDao mTokenDao;

    TokenModelRepository() {
        super(5);
        OstSdkDatabase db = OstSdkDatabase.getDatabase();
        mTokenDao = db.tokenDao();
    }

    @Override
    BaseDao getModel() {
        return mTokenDao;
    }


    @Override
    public OstToken registerToken(JSONObject jsonObject, TaskCallback callback) throws JSONException {
        OstToken ostToken = new OstToken(jsonObject);
        super.insert(ostToken, callback);
        return ostToken;
    }

    @Override
    public OstToken getTokenById(String id) {
        return (OstToken) super.getById(id);
    }
}