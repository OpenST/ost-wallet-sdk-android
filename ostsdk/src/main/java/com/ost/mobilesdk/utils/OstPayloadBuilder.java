package com.ost.mobilesdk.utils;

import android.util.Log;

import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class OstPayloadBuilder {
    private static final String TAG = "OstPayloadBuilder";

    public static final String DATA_DEFINATION = "data_defination";
    public static final String TO = "to";
    private static final String NULL_ADDRESS = "0x0000000000000000000000000000000000000000";
    public static final String VALUE = "value";
    public static final String CALL_DATA = "calldata";
    public static final String RAW_CALL_DATA = "raw_calldata";
    public static final String OPERATION = "operation";
    public static final String SAFE_TXN_GAS = "safe_tx_gas";
    public static final String DATA_GAS = "data_gas";
    public static final String GAS_PRICE = "gas_price";
    public static final String GAS_TOKEN = "gas_token";
    public static final String REFUND_RECEIVER = "refund_receiver";
    public static final String SIGNATURES = "signatures";
    public static final String SIGNER = "signer";
    private String dataDefination = OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE.toUpperCase();
    private String to = NULL_ADDRESS;

    public static Map<String, Object> getPayloadMap(JSONObject payload) throws JSONException {

        Map<String, Object> map = new HashMap<>();
        map.put(DATA_DEFINATION, payload.getString(DATA_DEFINATION));
        map.put(TO, payload.getString(TO));
        map.put(VALUE, payload.getString(VALUE));
        map.put(CALL_DATA, payload.getString(CALL_DATA));

        JSONObject rawCallData = payload.getJSONObject(RAW_CALL_DATA);
        JSONArray jsonArray = rawCallData.getJSONArray("parameters");

        map.put(RAW_CALL_DATA, new GnosisSafe().getAddOwnerWithThresholdData(jsonArray.getString(0), jsonArray.getString(1)));
        map.put(OPERATION, payload.getString(OPERATION));
        map.put(SAFE_TXN_GAS, payload.getString(SAFE_TXN_GAS));
        map.put(DATA_GAS, payload.getString(DATA_GAS));
        map.put(GAS_TOKEN, payload.getString(GAS_TOKEN));
        map.put(GAS_PRICE, payload.getString(GAS_PRICE));
        map.put(REFUND_RECEIVER, payload.getString(REFUND_RECEIVER));
        map.put(SIGNATURES, payload.getString(SIGNATURES));
        map.put(SIGNER, payload.getString(SIGNER));

        return map;
    }

    public OstPayloadBuilder setDataDefination(String dataDefination) {
        this.dataDefination = dataDefination;
        return this;
    }

    public OstPayloadBuilder setTo(String to) {
        this.to = to;
        return this;
    }

    public OstPayloadBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public OstPayloadBuilder setCallData(String callData) {
        this.callData = callData;
        return this;
    }

    public OstPayloadBuilder setRawCalldata(JSONObject rawCalldata) {
        this.rawCalldata = rawCalldata;
        return this;
    }

    public OstPayloadBuilder setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public OstPayloadBuilder setSafeTxnGas(String safeTxnGas) {
        this.safeTxnGas = safeTxnGas;
        return this;
    }

    public OstPayloadBuilder setDataGas(String dataGas) {
        this.dataGas = dataGas;
        return this;
    }

    public OstPayloadBuilder setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
        return this;
    }

    public OstPayloadBuilder setGasToken(String gasToken) {
        this.gasToken = gasToken;
        return this;
    }

    public OstPayloadBuilder setRefundReceiver(String refundReceiver) {
        this.refundReceiver = refundReceiver;
        return this;
    }

    public OstPayloadBuilder setSignatures(String signatures) {
        this.signatures = signatures;
        return this;
    }

    public OstPayloadBuilder setSigner(String signer) {
        this.signer = signer;
        return this;
    }

    private String value = "0";
    private String callData = "0x0";
    private JSONObject rawCalldata = new JSONObject();
    private String operation = "0";
    private String safeTxnGas = "0";
    private String dataGas = "0";
    private String gasPrice = "0";
    private String gasToken = NULL_ADDRESS;
    private String refundReceiver = NULL_ADDRESS;
    private String signatures = "0x0";
    private String signer = "0x0";
    private String nonce = "0";

    public JSONObject build() {
        JSONObject payLoad = new JSONObject();
        try {
            payLoad.put(DATA_DEFINATION, dataDefination);
            payLoad.put(TO, to);
            payLoad.put(VALUE, value);
            payLoad.put(CALL_DATA, callData);
            payLoad.put(RAW_CALL_DATA, rawCalldata);
            payLoad.put(OPERATION, operation);
            payLoad.put(SAFE_TXN_GAS, safeTxnGas);
            payLoad.put(DATA_GAS, dataGas);
            payLoad.put(GAS_PRICE, gasPrice);
            payLoad.put(GAS_TOKEN, gasToken);
            payLoad.put(REFUND_RECEIVER, refundReceiver);
            payLoad.put(SIGNATURES, signatures);
            payLoad.put(SIGNER, signer);
        } catch (JSONException e) {
            Log.e(TAG, "Unexpected JSON Exception");
        }
        return payLoad;
    }

    public OstPayloadBuilder setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public static Map<String, Object> toMap(JSONObject jsonobj)  throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keys = jsonobj.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            Object value = jsonobj.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, String.valueOf(value));
        }   return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(String.valueOf(value));
        }   return list;
    }
}