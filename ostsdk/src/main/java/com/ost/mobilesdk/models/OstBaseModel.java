package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstBaseEntity;

public interface OstBaseModel {
    void insertOrUpdateEntity(OstBaseEntity ostBaseEntity);

    OstBaseEntity getEntityById(String id);

    OstBaseEntity[] getEntitiesByParentId(String id);

    void deleteEntity(String id);

    void deleteAllEntities();
}
