package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;

public interface OstDeviceManagerOperationModel extends OstBaseModel {
    @Override
    OstDeviceManagerOperation getEntityById(String id);

    @Override
    OstDeviceManagerOperation[] getEntitiesByParentId(String id);
}