/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.workflows;

public class OstContextEntity {
    private String message;
    private Object entity;
    private String entityType;

    public OstContextEntity(String message, Object entity, String entityType) {
        this.message = message;
        this.entity = entity;
        this.entityType = entityType;
    }

    public OstContextEntity(Object entity, String entityType) {
        this.message = "";
        this.entity = entity;
        this.entityType = entityType;
    }


    public String getMessage() {
        return message;
    }

    public Object getEntity() {
        return entity;
    }

    public String getEntityType() {
        return entityType;
    }
}