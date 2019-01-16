package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstRule;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstRuleModel {
    void insertRule(OstRule ostRule, OstTaskCallback callback);

    void insertAllRules(OstRule[] ostRule, OstTaskCallback callback);

    void deleteRule(String id, OstTaskCallback callback);

    OstRule[] getRulesByIds(String[] ids);

    OstRule getRuleById(String id);

    void deleteAllRules(OstTaskCallback callback);

    OstRule initRule(JSONObject jsonObject, OstTaskCallback callback) throws JSONException;
}
