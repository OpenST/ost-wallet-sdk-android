package com.ost.mobilesdk.utils;

import com.ost.mobilesdk.models.entities.OstDeviceManagerOperation;

import java.util.ArrayList;
import java.util.HashMap;
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
    public static final String SIGNERS = "signers";
    private String dataDefination = OstDeviceManagerOperation.KIND_TYPE.AUTHORIZE_DEVICE.toUpperCase();
    private String to = NULL_ADDRESS;

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

    public OstPayloadBuilder setRawCalldata(Map<String,Object> rawCalldata) {
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

    public OstPayloadBuilder setSigners(List<String> signers) {
        this.signers = signers;
        return this;
    }

    private String value = "0";
    private String callData = "0x0";
    private Map<String, Object> rawCalldata = new HashMap<>();
    private String operation = "0";
    private String safeTxnGas = "0";
    private String dataGas = "0";
    private String gasPrice = "0";
    private String gasToken = NULL_ADDRESS;
    private String refundReceiver = NULL_ADDRESS;
    private String signatures = "0x0";
    private List<String> signers = new ArrayList<>();
    private String nonce = "0";

    public Map<String, Object> build() {
        Map<String, Object> map = new HashMap<>();
        map.put(DATA_DEFINATION, dataDefination);
        map.put(TO, to);
        map.put(VALUE, value);
        map.put(CALL_DATA, callData);
        map.put(RAW_CALL_DATA, rawCalldata);
        map.put(OPERATION, operation);
        map.put(SAFE_TXN_GAS, safeTxnGas);
        map.put(DATA_GAS, dataGas);
        map.put(GAS_PRICE, gasPrice);
        map.put(GAS_TOKEN, gasToken);
        map.put(REFUND_RECEIVER, refundReceiver);
        map.put(SIGNATURES, signatures);
        map.put(SIGNERS, signers);
        return map;
    }

    public OstPayloadBuilder setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }
}