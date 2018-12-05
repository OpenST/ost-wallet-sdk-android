package com.ost.ostsdk.data.models;

import com.ost.ostsdk.data.models.entities.RuleEntity;

public interface RuleModel {
    void insertUser(RuleEntity ruleEntity, DbProcessCallback callback);

    void insertAllUsers(RuleEntity[] ruleEntity, DbProcessCallback callback);

    void deleteUser(RuleEntity ruleEntity, DbProcessCallback callback);

    RuleEntity getUsersByIds(double[] ids);

    RuleEntity getUserById(double id);

    void deleteAllUsers(DbProcessCallback callback);
}
