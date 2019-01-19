package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstUser;

public interface OstUserModel extends OstBaseModel {
    @Override
    OstUser getEntityById(String id);

    @Override
    OstUser[] getEntitiesByParentId(String id);
}
