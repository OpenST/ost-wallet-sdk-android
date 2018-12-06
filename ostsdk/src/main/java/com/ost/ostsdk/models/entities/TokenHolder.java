package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "token_holder")
public class TokenHolder extends BaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String WALLETS = "wallets";
    public static final String SESSIONS = "sessions";
    public static final String REQUIREMENTS = "requirements";
    public static final String EXECUTE_RULE_CALL_PREFIX = "execute_rule_call_prefix";
    public static final String AUTHORIZE_SESSION_CALL_PREFIX = "authorize_session_callprefix";

    private double userId;
    private String address;
    private String[] wallets;
    private String[] sessions;
    private int requirements;
    private String executeRuleCallPrefix;
    private String authorizeSessionCallPrefix;


    public TokenHolder(JSONObject jsonObject) {
        super(jsonObject);
    }

    private TokenHolder(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(TokenHolder.USER_ID) &&
                jsonObject.has(TokenHolder.ADDRESS) &&
                jsonObject.has(TokenHolder.WALLETS) &&
                jsonObject.has(TokenHolder.SESSIONS) &&
                jsonObject.has(TokenHolder.REQUIREMENTS) &&
                jsonObject.has(TokenHolder.EXECUTE_RULE_CALL_PREFIX) &&
                jsonObject.has(TokenHolder.AUTHORIZE_SESSION_CALL_PREFIX);


    }

    @Override
    public void processJson(JSONObject jsonObject) {
        super.processJson(jsonObject);
    }

    public double getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public String[] getWallets() {
        return wallets;
    }

    public String[] getSessions() {
        return sessions;
    }

    public int getRequirements() {
        return requirements;
    }

    public String getExecuteRuleCallPrefix() {
        return executeRuleCallPrefix;
    }

    public String getAuthorizeSessionCallPrefix() {
        return authorizeSessionCallPrefix;
    }

    public void setUserId(double userId) {
        this.userId = userId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWallets(String[] wallets) {
        this.wallets = wallets;
    }

    public void setSessions(String[] sessions) {
        this.sessions = sessions;
    }

    public void setRequirements(int requirements) {
        this.requirements = requirements;
    }

    public void setExecuteRuleCallPrefix(String executeRuleCallPrefix) {
        this.executeRuleCallPrefix = executeRuleCallPrefix;
    }

    public void setAuthorizeSessionCallPrefix(String authorizeSessionCallPrefix) {
        this.authorizeSessionCallPrefix = authorizeSessionCallPrefix;
    }
}
