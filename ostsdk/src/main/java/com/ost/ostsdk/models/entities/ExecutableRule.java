package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "executable_rule")
public class ExecutableRule extends BaseEntity {

    public static final String STATUS = "status";
    public static final String USER_ID = "user_id";
    public static final String RULE_ID = "rule_id";
    public static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    public static final String METHOD = "method";
    public static final String PARAMS = "params";
    public static final String SESSION = "session";
    public static final String EXECUTE_RULE_PAYLOAD = "execute_rule_payload";

    @Ignore
    private double userId;
    @Ignore
    private String tokenHolderAddress;
    @Ignore
    private double ruleId;
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


    public ExecutableRule(JSONObject jsonObject) {
        super(jsonObject);
    }

    public ExecutableRule() {
    }

    private ExecutableRule(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(ExecutableRule.USER_ID) &&
                jsonObject.has(ExecutableRule.STATUS) &&
                jsonObject.has(ExecutableRule.RULE_ID) &&
                jsonObject.has(ExecutableRule.TOKEN_HOLDER_ADDRESS) &&
                jsonObject.has(ExecutableRule.METHOD) &&
                jsonObject.has(ExecutableRule.PARAMS) &&
                jsonObject.has(ExecutableRule.EXECUTE_RULE_PAYLOAD) &&
                jsonObject.has(ExecutableRule.SESSION);


    }

    @Override
    public void processJson(JSONObject jsonObject) {
        super.processJson(jsonObject);
    }

    public double getUserId() {
        return userId;
    }

    private void setUserId(double userId) {
        this.userId = userId;
    }

    public String getTokenHolderAddress() {
        return tokenHolderAddress;
    }

    private void setTokenHolderAddress(String tokenHolderAddress) {
        this.tokenHolderAddress = tokenHolderAddress;
    }

    public double getRuleId() {
        return ruleId;
    }

    private void setRuleId(double ruleId) {
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
