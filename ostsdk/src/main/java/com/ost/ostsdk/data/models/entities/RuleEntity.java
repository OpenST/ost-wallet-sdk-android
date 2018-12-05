package com.ost.ostsdk.data.models.entities;

import android.arch.persistence.room.Entity;

import org.json.JSONObject;

@Entity(tableName = "rule")
public class RuleEntity extends BaseEntity {

    private double economyId;

    private String name;

    private String address;

    private double abi;

    public RuleEntity() {
        super();
    }

    public double getEconomyId() {
        return economyId;
    }

    public void setEconomyId(double economyId) {
        this.economyId = economyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getAbi() {
        return abi;
    }

    public void setAbi(double abi) {
        this.abi = abi;
    }

    @Override
    public void processJson(JSONObject data) {
        super.processJson(data);

    }
}
