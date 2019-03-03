package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.utils.AsyncStatus;

import java.util.concurrent.Future;

public interface OstBaseModel {
    Future<AsyncStatus> insertOrUpdateEntity(OstBaseEntity ostBaseEntity);

    OstBaseEntity getEntityById(String id);

    OstBaseEntity[] getEntitiesByParentId(String id);

    Future<AsyncStatus> deleteEntity(String id);

    Future<AsyncStatus> deleteAllEntities();
}
