package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstBaseEntity;

public interface OstBaseModel {
    void insertOrUpdateEntity(OstBaseEntity ostBaseEntity);

    OstBaseEntity getEntityById(String id);

    OstBaseEntity[] getEntitiesByParentId(String id);

    void deleteEntity(String id);

    void deleteAllEntities();
}
