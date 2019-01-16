package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "token_holder")
public class OstTokenHolder extends OstBaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String REQUIREMENTS = "requirements";
    public static final String EXECUTE_RULE_CALL_PREFIX = "execute_rule_callprefix";

    @Ignore
    private String userId;
    @Ignore
    private String address;
    @Ignore
    private int requirements;
    @Ignore
    private String executeRuleCallPrefix;

    public OstTokenHolder(String id, String parentId, JSONObject data, String status, double updatedTimestamp) {
        super(id, parentId, data, status, updatedTimestamp);
    }

    @Ignore
    public OstTokenHolder(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstTokenHolder.USER_ID) &&
                jsonObject.has(OstTokenHolder.ADDRESS) &&
                jsonObject.has(OstTokenHolder.REQUIREMENTS) &&
                jsonObject.has(OstTokenHolder.EXECUTE_RULE_CALL_PREFIX);


    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setUserId(jsonObject.getString(OstTokenHolder.USER_ID));
        setAddress(jsonObject.getString(OstTokenHolder.ADDRESS));

        setRequirements(jsonObject.getInt(OstTokenHolder.REQUIREMENTS));
        setExecuteRuleCallPrefix(jsonObject.getString(OstTokenHolder.EXECUTE_RULE_CALL_PREFIX));
    }

    public String getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public int getRequirements() {
        return requirements;
    }

    public String getExecuteRuleCallPrefix() {
        return executeRuleCallPrefix;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setRequirements(int requirements) {
        this.requirements = requirements;
    }

    private void setExecuteRuleCallPrefix(String executeRuleCallPrefix) {
        this.executeRuleCallPrefix = executeRuleCallPrefix;
    }

    public OstSession getDeviceTokenHolderSession() throws Exception {
        OstSession deviceSession = null;
        OstSession sessions[] = OstModelFactory.getTokenHolderSession().getTokenHolderSessionsByParentId(getId());
        for (OstSession session : sessions) {
            if (null != new OstSecureKeyModelRepository().getById(session.getAddress())) {
                deviceSession = session;
                break;
            }
        }
        if (null == deviceSession) {
            throw new Exception("Wallet not found in db");
        }
        return deviceSession;
    }
}
