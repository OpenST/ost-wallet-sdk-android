package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstCredits;

public interface OstCreditsModel extends OstBaseModel{
    @Override
    OstCredits getEntityById(String id);

    @Override
    OstCredits[] getEntitiesByParentId(String id);
}