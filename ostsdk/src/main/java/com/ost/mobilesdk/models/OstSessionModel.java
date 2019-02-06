package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstSession;

public interface OstSessionModel extends OstBaseModel {
    @Override
    OstSession getEntityById(String id);


    @Override
    OstSession[] getEntitiesByParentId(String id);
}