package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstDevice;

public interface OstDeviceModel extends OstBaseModel {
    @Override
    OstDevice getEntityById(String id);

    @Override
    OstDevice[] getEntitiesByParentId(String id);
}