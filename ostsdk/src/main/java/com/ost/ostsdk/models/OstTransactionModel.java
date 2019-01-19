package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstTransaction;

public interface OstTransactionModel extends OstBaseModel {
    @Override
    OstTransaction getEntityById(String id);

    @Override
    OstTransaction[] getEntitiesByParentId(String id);
}