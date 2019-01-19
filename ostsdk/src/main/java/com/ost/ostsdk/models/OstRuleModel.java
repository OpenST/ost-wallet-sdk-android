package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstRule;

public interface OstRuleModel extends OstBaseModel {
    @Override
    OstRule getEntityById(String id);

    @Override
    OstRule[] getEntitiesByParentId(String id);
}
