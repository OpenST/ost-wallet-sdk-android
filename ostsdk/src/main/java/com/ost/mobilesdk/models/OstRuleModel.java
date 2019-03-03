package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstRule;

public interface OstRuleModel extends OstBaseModel {
    @Override
    OstRule getEntityById(String id);

    @Override
    OstRule[] getEntitiesByParentId(String id);
}
