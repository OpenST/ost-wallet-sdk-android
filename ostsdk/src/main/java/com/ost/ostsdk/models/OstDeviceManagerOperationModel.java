package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceManagerOperation;

public interface OstDeviceManagerOperationModel extends OstBaseModel {
    @Override
    OstDeviceManagerOperation getEntityById(String id);

    @Override
    OstDeviceManagerOperation[] getEntitiesByParentId(String id);
}