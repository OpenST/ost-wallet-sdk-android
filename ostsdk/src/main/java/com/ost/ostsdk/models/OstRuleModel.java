package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstRule;

public interface OstRuleModel {
    void insertRule(OstRule ostRule);

    void insertAllRules(OstRule[] ostRule);

    void deleteRule(String id);

    OstRule[] getRulesByIds(String[] ids);

    OstRule getRuleById(String id);

    void deleteAllRules();

    OstRule insert(OstRule ostRule);
}
