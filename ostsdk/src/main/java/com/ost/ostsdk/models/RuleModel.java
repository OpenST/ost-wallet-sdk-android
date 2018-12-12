package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.Rule;

import org.json.JSONException;
import org.json.JSONObject;

public interface RuleModel {
    void insertRule(Rule rule, TaskCompleteCallback callback);

    void insertAllRules(Rule[] rule, TaskCompleteCallback callback);

    void deleteRule(Rule rule, TaskCompleteCallback callback);

    Rule[] getRulesByIds(String[] ids);

    Rule getRuleById(String id);

    void deleteAllRules(TaskCompleteCallback callback);

    Rule initRule(JSONObject jsonObject) throws JSONException;
}
