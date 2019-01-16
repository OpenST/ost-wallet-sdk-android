package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstExecutableRule;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstExecutableRuleModel {

    void insertExecutableRule(OstExecutableRule executableRule, OstTaskCallback callback);

    void insertAllExecutableRules(OstExecutableRule[] executableRule, OstTaskCallback callback);

    void deleteExecutableRule(String id, OstTaskCallback callback);

    OstExecutableRule[] getExecutableRulesByIds(String[] ids);

    OstExecutableRule getExecutableRuleById(String id);

    void deleteAllExecutableRules(OstTaskCallback callback);

    OstExecutableRule initExecutableRule(JSONObject jsonObject) throws JSONException;
}