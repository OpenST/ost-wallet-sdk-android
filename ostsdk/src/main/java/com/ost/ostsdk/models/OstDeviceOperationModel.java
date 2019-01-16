package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceOperation;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstDeviceOperationModel {

    void insertMultiSigOperation(OstDeviceOperation ostDeviceOperation);

    void insertAllMultiSigOperations(OstDeviceOperation[] ostDeviceOperation);

    void deleteMultiSigOperation(String id);

    OstDeviceOperation[] getMultiSigOperationsByIds(String[] ids);

    OstDeviceOperation getMultiSigOperationById(String id);

    void deleteAllMultiSigOperations();

    OstDeviceOperation initMultiSigOperation(JSONObject jsonObject) throws JSONException;
}