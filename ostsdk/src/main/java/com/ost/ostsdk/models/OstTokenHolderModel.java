package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstTokenHolder;

public interface OstTokenHolderModel extends OstBaseModel {
    @Override
    OstTokenHolder getEntityById(String id);

    @Override
    OstTokenHolder[] getEntitiesByParentId(String id);
}