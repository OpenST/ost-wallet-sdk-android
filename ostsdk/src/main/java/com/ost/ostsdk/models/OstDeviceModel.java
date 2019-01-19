package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDevice;

public interface OstDeviceModel extends OstBaseModel {
    @Override
    OstDevice getEntityById(String id);

    @Override
    OstDevice[] getEntitiesByParentId(String id);
}