package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstTransaction;

public interface OstTransactionModel extends OstBaseModel {
    @Override
    OstTransaction getEntityById(String id);

    @Override
    OstTransaction[] getEntitiesByParentId(String id);
}