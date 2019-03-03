package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstToken;

public interface OstTokenModel extends OstBaseModel {
    @Override
    OstToken getEntityById(String id);

    @Override
    OstToken[] getEntitiesByParentId(String id);
}