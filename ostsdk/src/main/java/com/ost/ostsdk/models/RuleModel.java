package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.Rule;

import org.json.JSONException;
import org.json.JSONObject;

public interface RuleModel {
    void insertRule(Rule rule, TaskCallback callback);

    void insertAllRules(Rule[] rule, TaskCallback callback);

    void deleteRule(Rule rule, TaskCallback callback);

    Rule[] getRulesByIds(String[] ids);

    Rule getRuleById(String id);

    void deleteAllRules(TaskCallback callback);

    Rule initRule(JSONObject jsonObject) throws JSONException;
}
