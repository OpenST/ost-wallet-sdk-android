package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.MultiSigOperation;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigOperationModel {

    void insertMultiSigOperation(MultiSigOperation multiSigOperation, TaskCallback callback);

    void insertAllMultiSigOperations(MultiSigOperation[] multiSigOperation, TaskCallback callback);

    void deleteMultiSigOperation(String id, TaskCallback callback);

    MultiSigOperation[] getMultiSigOperationsByIds(String[] ids);

    MultiSigOperation getMultiSigOperationById(String id);

    void deleteAllMultiSigOperations(TaskCallback callback);

    MultiSigOperation initMultiSigOperation(JSONObject jsonObject) throws JSONException;
}