package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceOperation;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigOperationModel {

    void insertMultiSigOperation(OstDeviceOperation ostDeviceOperation, TaskCallback callback);

    void insertAllMultiSigOperations(OstDeviceOperation[] ostDeviceOperation, TaskCallback callback);

    void deleteMultiSigOperation(String id, TaskCallback callback);

    OstDeviceOperation[] getMultiSigOperationsByIds(String[] ids);

    OstDeviceOperation getMultiSigOperationById(String id);

    void deleteAllMultiSigOperations(TaskCallback callback);

    OstDeviceOperation initMultiSigOperation(JSONObject jsonObject) throws JSONException;
}