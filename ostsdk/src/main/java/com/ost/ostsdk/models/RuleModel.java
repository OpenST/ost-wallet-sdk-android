package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.Rule;

public interface RuleModel {
    void insertUser(Rule rule, TaskCompleteCallback callback);

    void insertAllUsers(Rule[] rule, TaskCompleteCallback callback);

    void deleteUser(Rule rule, TaskCompleteCallback callback);

    Rule[] getUsersByIds(String[] ids);

    Rule getUserById(String id);

    void deleteAllUsers(TaskCompleteCallback callback);
}
