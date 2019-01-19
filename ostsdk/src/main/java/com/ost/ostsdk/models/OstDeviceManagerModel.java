package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceManager;

public interface OstDeviceManagerModel extends OstBaseModel {
    @Override
    OstDeviceManager getEntityById(String id);

    @Override
    OstDeviceManager[] getEntitiesByParentId(String id);
}