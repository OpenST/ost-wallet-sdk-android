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