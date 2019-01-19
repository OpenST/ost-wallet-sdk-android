package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstSession;

public interface OstSessionModel extends OstBaseModel {
    @Override
    OstSession getEntityById(String id);


    @Override
    OstSession[] getEntitiesByParentId(String id);
}