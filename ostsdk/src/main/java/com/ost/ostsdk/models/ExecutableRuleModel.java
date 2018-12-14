package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.ExecutableRule;

import org.json.JSONException;
import org.json.JSONObject;

public interface ExecutableRuleModel {

    void insertExecutableRule(ExecutableRule executableRule, TaskCallback callback);

    void insertAllExecutableRules(ExecutableRule[] executableRule, TaskCallback callback);

    void deleteExecutableRule(ExecutableRule executableRule, TaskCallback callback);

    ExecutableRule[] getExecutableRulesByIds(String[] ids);

    ExecutableRule getExecutableRuleById(String id);

    void deleteAllExecutableRules(TaskCallback callback);

    ExecutableRule initExecutableRule(JSONObject jsonObject) throws JSONException;
}