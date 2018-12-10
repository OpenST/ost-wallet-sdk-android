package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.Economy;

import org.json.JSONObject;

public interface EconomyModel {
    Economy registerEconomy(JSONObject jsonObject);

    Economy getEconomyById(String id);
}
