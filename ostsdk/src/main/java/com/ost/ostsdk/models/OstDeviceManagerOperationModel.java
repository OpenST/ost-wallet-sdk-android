package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceManagerOperation;

public interface OstDeviceManagerOperationModel {

    void insertMultiSigOperation(OstDeviceManagerOperation ostDeviceManagerOperation);

    void insertAllMultiSigOperations(OstDeviceManagerOperation[] ostDeviceManagerOperation);

    void deleteMultiSigOperation(String id);

    OstDeviceManagerOperation[] getMultiSigOperationsByIds(String[] ids);

    OstDeviceManagerOperation getMultiSigOperationById(String id);

    void deleteAllMultiSigOperations();

    OstDeviceManagerOperation insert(OstDeviceManagerOperation ostDeviceManagerOperation);
}