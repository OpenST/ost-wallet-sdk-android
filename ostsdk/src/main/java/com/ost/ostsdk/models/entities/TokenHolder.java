package com.ost.ostsdk.models.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONException;
import org.json.JSONObject;


@Entity(tableName = "token_holder")
public class TokenHolder extends BaseEntity {

    public static final String USER_ID = "user_id";
    public static final String ADDRESS = "address";
    public static final String WALLETS = "wallets";
    public static final String SESSIONS = "sessions";
    public static final String REQUIREMENTS = "requirements";
    public static final String EXECUTE_RULE_CALL_PREFIX = "execute_rule_callprefix";

    @Ignore
    private String userId;
    @Ignore
    private String address;
    @Ignore
    private String[] wallets;
    @Ignore
    private String[] sessions;
    @Ignore
    private int requirements;
    @Ignore
    private String executeRuleCallPrefix;

    public TokenHolder(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    private TokenHolder(String jsonString) throws JSONException {
        super(new JSONObject(jsonString));
    }

    public TokenHolder() {
    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(TokenHolder.USER_ID) &&
                jsonObject.has(TokenHolder.ADDRESS) &&
                jsonObject.has(TokenHolder.WALLETS) &&
                jsonObject.has(TokenHolder.SESSIONS) &&
                jsonObject.has(TokenHolder.REQUIREMENTS) &&
                jsonObject.has(TokenHolder.EXECUTE_RULE_CALL_PREFIX);


    }

    @Override
    public void processJson(JSONObject jsonObject) throws JSONException {
        super.processJson(jsonObject);
    }

    public String getUserId() {
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

    private void setUserId(String userId) {
        this.userId = userId;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setWallets(String[] wallets) {
        this.wallets = wallets;
    }

    private void setSessions(String[] sessions) {
        this.sessions = sessions;
    }

    private void setRequirements(int requirements) {
        this.requirements = requirements;
    }

    private void setExecuteRuleCallPrefix(String executeRuleCallPrefix) {
        this.executeRuleCallPrefix = executeRuleCallPrefix;
    }
}
