package com.ost.mobilesdk.models;

import com.ost.mobilesdk.models.entities.OstDeviceManager;

public interface OstDeviceManagerModel extends OstBaseModel {
    @Override
    OstDeviceManager getEntityById(String id);

    @Override
    OstDeviceManager[] getEntitiesByParentId(String id);
}