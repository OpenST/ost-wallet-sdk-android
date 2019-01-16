package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstDeviceOperation;

import org.json.JSONException;
import org.json.JSONObject;

public interface OstDeviceOperationModel {

    void insertMultiSigOperation(OstDeviceOperation ostDeviceOperation, OstTaskCallback callback);

    void insertAllMultiSigOperations(OstDeviceOperation[] ostDeviceOperation, OstTaskCallback callback);

    void deleteMultiSigOperation(String id, OstTaskCallback callback);

    OstDeviceOperation[] getMultiSigOperationsByIds(String[] ids);

    OstDeviceOperation getMultiSigOperationById(String id);

    void deleteAllMultiSigOperations(OstTaskCallback callback);

    OstDeviceOperation initMultiSigOperation(JSONObject jsonObject) throws JSONException;
}