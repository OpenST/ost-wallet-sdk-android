package com.ost.ostsdk.models.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import org.json.JSONObject;

@Entity(tableName = "rule")
public class Rule extends BaseEntity {

    public static final String ECONOMY_ID = "economy_id";
    public static final String ADDRESS = "address";
    public static final String NAME = "name";
    public static final String ABI = "abi";

    @Ignore
    private double economyId;
    @Ignore
    private String name;
    @Ignore
    private String address;
    @Ignore
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


    private void setEconomyId(double economyId) {
        this.economyId = economyId;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setAbi(String abi) {
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
