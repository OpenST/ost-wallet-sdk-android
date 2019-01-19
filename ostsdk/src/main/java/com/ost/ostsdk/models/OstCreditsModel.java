package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstCredits;

public interface OstCreditsModel extends OstBaseModel{
    @Override
    OstCredits getEntityById(String id);

    @Override
    OstCredits[] getEntitiesByParentId(String id);
}