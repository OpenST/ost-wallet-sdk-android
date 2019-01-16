package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstExecutableRule;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstExecutableRuleModel {

    void insertExecutableRule(OstExecutableRule executableRule);

    void insertAllExecutableRules(OstExecutableRule[] executableRule);

    void deleteExecutableRule(String id);

    OstExecutableRule[] getExecutableRulesByIds(String[] ids);

    OstExecutableRule getExecutableRuleById(String id);

    void deleteAllExecutableRules();

    OstExecutableRule initExecutableRule(JSONObject jsonObject) throws JSONException;
}