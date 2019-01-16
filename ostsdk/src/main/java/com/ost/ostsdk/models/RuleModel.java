package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstRule;

import org.json.JSONException;
import org.json.JSONObject;

public interface RuleModel {
    void insertRule(OstRule ostRule, TaskCallback callback);

    void insertAllRules(OstRule[] ostRule, TaskCallback callback);

    void deleteRule(String id, TaskCallback callback);

    OstRule[] getRulesByIds(String[] ids);

    OstRule getRuleById(String id);

    void deleteAllRules(TaskCallback callback);

    OstRule initRule(JSONObject jsonObject, TaskCallback callback) throws JSONException;
}
