package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.Economy;

import org.json.JSONException;
import org.json.JSONObject;

public interface EconomyModel {

    Economy registerEconomy(JSONObject jsonObject, TaskCallback callback) throws JSONException;

    Economy getEconomyById(String id);
}
