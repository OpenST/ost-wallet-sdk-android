package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstUser;

public interface OstUserModel extends OstBaseModel {
    @Override
    OstUser getEntityById(String id);

    @Override
    OstUser[] getEntitiesByParentId(String id);
}
