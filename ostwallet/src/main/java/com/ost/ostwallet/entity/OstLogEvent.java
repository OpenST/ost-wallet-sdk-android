/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * To hold Device info
 */
@Entity(tableName = "log_events")
public class OstLogEvent {

    public static final String TAG = "OstLogEvent";

    @PrimaryKey()
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "workflow_id")
    private String workflowId;

    @ColumnInfo(name = "workflow")
    private String workflow;

    @ColumnInfo(name = "callback_name")
    private String callbackName;

    @ColumnInfo(name = "details")
    private String details;

    public OstLogEvent(String workflowId, String workflow, String callbackName, String details) {
        id = System.currentTimeMillis();
        this.workflowId = workflowId;
        this.workflow = workflow;
        this.callbackName = callbackName;
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getWorkflow() {
        return workflow;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public String getDetails() {
        return details;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}