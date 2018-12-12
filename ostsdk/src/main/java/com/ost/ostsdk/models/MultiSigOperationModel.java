package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.MultiSigOperation;

import org.json.JSONException;
import org.json.JSONObject;

public interface MultiSigOperationModel {

    void insertMultiSigOperation(MultiSigOperation multiSigOperation, TaskCompleteCallback callback);

    void insertAllMultiSigOperations(MultiSigOperation[] multiSigOperation, TaskCompleteCallback callback);

    void deleteMultiSigOperation(MultiSigOperation multiSigOperation, TaskCompleteCallback callback);

    MultiSigOperation[] getMultiSigOperationsByIds(String[] ids);

    MultiSigOperation getMultiSigOperationById(String id);

    void deleteAllMultiSigOperations(TaskCompleteCallback callback);

    MultiSigOperation initMultiSigOperation(JSONObject jsonObject) throws JSONException;
}