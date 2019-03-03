package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstTokenHolder;

public interface OstTokenHolderModel extends OstBaseModel {
    @Override
    OstTokenHolder getEntityById(String id);

    @Override
    OstTokenHolder[] getEntitiesByParentId(String id);
}