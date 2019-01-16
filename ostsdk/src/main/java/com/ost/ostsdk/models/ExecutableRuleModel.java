package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstExecutableRule;

import org.json.JSONException;
import org.json.JSONObject;

public interface ExecutableRuleModel {

    void insertExecutableRule(OstExecutableRule executableRule, TaskCallback callback);

    void insertAllExecutableRules(OstExecutableRule[] executableRule, TaskCallback callback);

    void deleteExecutableRule(String id, TaskCallback callback);

    OstExecutableRule[] getExecutableRulesByIds(String[] ids);

    OstExecutableRule getExecutableRuleById(String id);

    void deleteAllExecutableRules(TaskCallback callback);

    OstExecutableRule initExecutableRule(JSONObject jsonObject) throws JSONException;
}