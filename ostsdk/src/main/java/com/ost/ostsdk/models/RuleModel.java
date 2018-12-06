package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.Rule;

public interface RuleModel {
    void insertUser(Rule rule, DbProcessCallback callback);

    void insertAllUsers(Rule[] rule, DbProcessCallback callback);

    void deleteUser(Rule rule, DbProcessCallback callback);

    Rule getUsersByIds(double[] ids);

    Rule getUserById(double id);

    void deleteAllUsers(DbProcessCallback callback);
}
