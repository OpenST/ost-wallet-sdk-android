package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;

import org.json.JSONObject;

@Entity(tableName = "rule")
public class Rule extends BaseEntity {

    public static final String ECONOMY_ID = "economy_id";
    public static final String ADDRESS = "address";
    public static final String NAME = "name";
    public static final String ABI = "abi";

    private double economyId;

    private String name;

    private String address;

    private String abi;

    public Rule() {
        super();
    }

    public double getEconomyId() {
        return economyId;
    }

    public String getAddress() {
        return address;
    }

    public String getAbi() {
        return abi;
    }

    public String getName() {
        return name;
    }


    public void setEconomyId(double economyId) {
        this.economyId = economyId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }

    @Override
    public void processJson(JSONObject data) {
        super.processJson(data);

    }

    @Override
    boolean validate(JSONObject jsonObject) {
        return super.validate(jsonObject) &&
                jsonObject.has(Rule.ECONOMY_ID) &&
                jsonObject.has(Rule.NAME) &&
                jsonObject.has(Rule.ABI) &&
                jsonObject.has(Rule.ADDRESS);
    }
}
