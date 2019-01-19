package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstToken;

public interface OstTokenModel extends OstBaseModel {
    @Override
    OstToken getEntityById(String id);

    @Override
    OstToken[] getEntitiesByParentId(String id);
}