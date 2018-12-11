package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.ExecutableRule;

import org.json.JSONObject;

public interface ExecutableRuleModel {

    void insertExecutableRule(ExecutableRule executableRule, TaskCompleteCallback callback);

    void insertAllExecutableRules(ExecutableRule[] executableRule, TaskCompleteCallback callback);

    void deleteExecutableRule(ExecutableRule executableRule, TaskCompleteCallback callback);

    ExecutableRule[] getExecutableRulesByIds(String[] ids);

    ExecutableRule getExecutableRuleById(String id);

    void deleteAllExecutableRules(TaskCompleteCallback callback);

    ExecutableRule initExecutableRule(JSONObject jsonObject);
}