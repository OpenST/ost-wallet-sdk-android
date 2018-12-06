package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "executable_rule")
public class ExecutableRule extends BaseEntity {

    public static final String STATUS = "status";
    public static final String USER_ID = "user_id";
    public static final String RULE_ID = "rule_id";
    public static final String LOCAL_ENTITY_ID = "local_entity_id";
    public static final String TOKEN_HOLDER_ADDRESS = "token_holder_address";
    public static final String METHOD = "method";
    public static final String PARAMS = "params";
    public static final String SESSION = "session";
    public static final String EXECUTE_RULE_PAYLOAD = "execute_rule_payload";


    private double localEntityId;
    private double userId;
    private String tokenHolderAddress;
    private double ruleId;
    private String method;
    private String params;
    private String session;
    private JSONObject executeRulePayload;
    private String status;


    public ExecutableRule(JSONObject jsonObject) {
        super(jsonObject);
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
                jsonObject.has(ExecutableRule.LOCAL_ENTITY_ID) &&
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

    public double getLocalEntityId() {
        return localEntityId;
    }

    public void setLocalEntityId(double localEntityId) {
        this.localEntityId = localEntityId;
    }

    public double getUserId() {
        return userId;
    }

    public void setUserId(double userId) {
        this.userId = userId;
    }

    public String getTokenHolderAddress() {
        return tokenHolderAddress;
    }

    public void setTokenHolderAddress(String tokenHolderAddress) {
        this.tokenHolderAddress = tokenHolderAddress;
    }

    public double getRuleId() {
        return ruleId;
    }

    public void setRuleId(double ruleId) {
        this.ruleId = ruleId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public JSONObject getExecuteRulePayload() {
        return executeRulePayload;
    }

    public void setExecuteRulePayload(JSONObject executeRulePayload) {
        this.executeRulePayload = executeRulePayload;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }
}
