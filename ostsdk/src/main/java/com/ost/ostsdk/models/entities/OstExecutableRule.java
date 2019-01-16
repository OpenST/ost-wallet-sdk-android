package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "executable_rule")
public class OstExecutableRule extends OstBaseEntity {

    public static final String STATUS = "status";
    public static final String USER_ID = "user_id";
    public static final String RULE_ID = "rule_id";
    public static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    public static final String METHOD = "method";
    public static final String PARAMS = "params";
    public static final String SESSION = "session";
    public static final String EXECUTE_RULE_PAYLOAD = "execute_rule_payload";

    @Ignore
    private String userId;
    @Ignore
    private String tokenHolderAddress;
    @Ignore
    private String ruleId;
    @Ignore
    private String method;
    @Ignore
    private String params;
    @Ignore
    private String session;
    @Ignore
    private JSONObject executeRulePayload;
    @Ignore
    private String status;


    public OstExecutableRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    public OstExecutableRule() {
    }

    private OstExecutableRule(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(OstExecutableRule.USER_ID) &&
                jsonObject.has(OstExecutableRule.STATUS) &&
                jsonObject.has(OstExecutableRule.RULE_ID) &&
                jsonObject.has(OstExecutableRule.TOKEN_HOLDER_ADDRESS) &&
                jsonObject.has(OstExecutableRule.METHOD) &&
                jsonObject.has(OstExecutableRule.PARAMS) &&
                jsonObject.has(OstExecutableRule.EXECUTE_RULE_PAYLOAD) &&
                jsonObject.has(OstExecutableRule.SESSION);
    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
        setExecuteRulePayload(jsonObject.getJSONObject(OstExecutableRule.EXECUTE_RULE_PAYLOAD));
        setMethod(jsonObject.getString(OstExecutableRule.METHOD));
        setParams(jsonObject.getString(OstExecutableRule.PARAMS));
        setRuleId(jsonObject.getString(OstExecutableRule.RULE_ID));
        setTokenHolderAddress(jsonObject.getString(OstExecutableRule.TOKEN_HOLDER_ADDRESS));
        setStatus(jsonObject.getString(OstExecutableRule.STATUS));
        setUserId(jsonObject.getString(OstExecutableRule.USER_ID));
        setSession(jsonObject.getString(OstExecutableRule.SESSION));
    }

    public String getUserId() {
        return userId;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenHolderAddress() {
        return tokenHolderAddress;
    }

    private void setTokenHolderAddress(String tokenHolderAddress) {
        this.tokenHolderAddress = tokenHolderAddress;
    }

    public String getRuleId() {
        return ruleId;
    }

    private void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getMethod() {
        return method;
    }

    private void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    private void setParams(String params) {
        this.params = params;
    }

    public String getSession() {
        return session;
    }

    private void setSession(String session) {
        this.session = session;
    }

    public JSONObject getExecuteRulePayload() {
        return executeRulePayload;
    }

    private void setExecuteRulePayload(JSONObject executeRulePayload) {
        this.executeRulePayload = executeRulePayload;
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }
}
