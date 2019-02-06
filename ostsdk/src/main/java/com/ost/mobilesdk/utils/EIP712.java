package com.ost.mobilesdk.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EIP712 {

//Schema as defined by EIP-712
//private static final JSONObject TYPED_DATA_JSON_SCHEMA = new JSONObject("{\n" +
//        "        type: 'object',\n" +
//        "                properties: {\n" +
//        "            types: {\n" +
//        "                type: 'object',\n" +
//        "                        properties: {\n" +
//        "                    EIP712Domain: { type: 'array' }\n" +
//        "                },\n" +
//        "                additionalProperties: {\n" +
//        "                    type: 'array',\n" +
//        "                            items: {\n" +
//        "                        type: 'object',\n" +
//        "                                properties: {\n" +
//        "                            name: { type: 'string' },\n" +
//        "                            type: { type: 'string' }\n" +
//        "                        },\n" +
//        "                        required: ['name', 'type']\n" +
//        "                    }\n" +
//        "                },\n" +
//        "                required: ['EIP712Domain']\n" +
//        "            },\n" +
//        "            primaryType: { type: 'string' },\n" +
//        "            domain: { type: 'object' },\n" +
//        "            message: { type: 'object' }\n" +
//        "        },\n" +
//        "        required: ['types', 'primaryType', 'domain', 'message']\n" +
//        "    }");
    private static final String TYPES = "types";
    private static final String PRIMARY_TYPE = "primaryType";
    private static final String DOMAIN = "domain";
    private static final String MESSAGE = "message";
    private static final String INITIAL_BYTE = "0x19";
    private static final String VERSION = "0x01";
    private static JSONArray DEFAULT_EIP712_DOMAIN_TYPE;

    static {
        try {
            DEFAULT_EIP712_DOMAIN_TYPE = new JSONArray("[{ name: 'verifyingContract', type: 'address' }]");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject types;
    private JSONObject message;
    private JSONObject domain;
    private String primaryType;

    public EIP712(JSONObject txnObject) throws JSONException {

        this.setTypes(txnObject.getJSONObject(TYPES));
        this.setPrimaryType(txnObject.getString(PRIMARY_TYPE));
        this.setDomain(txnObject.getJSONObject(DOMAIN));
        this.setMessage(txnObject.getJSONObject(MESSAGE));
    }

    private void setMessage(JSONObject jsonObject) {
        this.message = jsonObject;
    }

    private void setDomain(JSONObject jsonObject) {
        this.domain = jsonObject;
    }

    private void setPrimaryType(String jsonObject) {
        this.primaryType = jsonObject;
    }

    private void setTypes(JSONObject jsonObject) throws JSONException {
        if (null == jsonObject) {
            jsonObject = new JSONObject();
        }
        this.types = jsonObject;
        if (null == this.types.optJSONArray("EIP712Domain")) {
            this.types.put("EIP712Domain", DEFAULT_EIP712_DOMAIN_TYPE);
        }
    }

    //Method to add another data-type to the 'types' object.
    private void setDataType(String dataType, JSONArray dataTypeProperties) throws JSONException {
        types.put(dataType, dataTypeProperties);
    }

    private JSONArray getDataType(String dataType) throws JSONException {
        return types.optJSONArray(dataType);
    }

    private List<String> getDataTypeDependencies(String dataType, List<String> found) throws JSONException {

        if (found.contains(dataType)) {
            return found;
        }

        JSONArray dataTypeProperties = this.getDataType(dataType);
        if (null == dataTypeProperties) {
            return found;
        }
        found.add(dataType);

        for (int i = 0; i < dataTypeProperties.length(); i++) {
            JSONObject field = dataTypeProperties.getJSONObject(i);
            List<String> dependencies = this.getDataTypeDependencies(field.getString("type"), found);
            for (String dep : dependencies) {
                if (!found.contains(dep)) {
                    found.add(dep);
                }
            }
        }

        return found;
    }

    private List<String> getDataTypeDependencies(String dataType) throws JSONException {
        return getDataTypeDependencies(dataType, new ArrayList<>());
    }

    //Method to encode dataType
    public String encodeDataType(String dataType) throws JSONException {

    /*
      Find out dependencies
    */
        List<String> deps = this.getDataTypeDependencies(dataType);
    /*
      Sorting Logic:
      a. filter out input dataType from dependencies.
      b. Sort the dependencies
      b. Creates new dependencies array with dataType as first element.
    */
        List<String> tempDeps = new ArrayList<>();
        for (String dep : deps) {
            if (!dep.equals(dataType)) {
                tempDeps.add(dep);
            }
        }
        Collections.sort(tempDeps);
        deps = new ArrayList<>();
        deps.add(dataType);
        deps.addAll(tempDeps);

        StringBuilder result = new StringBuilder();
        for (String type : deps) {
            result.append(type);
            result.append("(");
            JSONArray jsonArray = this.getDataType(type);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                result.append(jsonObject.getString("type"));
                result.append(" ");
                result.append(jsonObject.getString("name"));
                if (i < jsonArray.length() - 1) {
                    result.append(",");
                }
            }
            result.append(")");
        }
        return result.toString();
    }

    //Method to hash dataType; not the data of the data type
    public String hashDataType(String dataType) throws JSONException {
        String encodedDataType = this.encodeDataType(dataType);
        byte[] byteArray = encodedDataType.getBytes();
        return Numeric.toHexString(Hash.sha3(byteArray));
    }

    //Method to encode data
    public String encodeData(String dataType, JSONObject data) throws Exception {


        // Add field contents
        JSONObject types = this.getTypes();
        JSONArray dataTypeProperties = this.getDataType(dataType);
        List<Type> abiTypes = new ArrayList<>();

        abiTypes.add(new ABITypeFactory().getType("bytes32", this.hashDataType(dataType)));

        for (int i = 0; i < dataTypeProperties.length(); i++) {
            JSONObject field = dataTypeProperties.getJSONObject(i);
            String fieldName = field.getString("name");
            String fieldType = field.getString("type");
            Object value = data.get(fieldName);
            if ("string".equals(fieldType) || "bytes".equals(fieldType)) {
                value = Hash.sha3String(((String) value));
                fieldType = "bytes32";
            } else if (null != getDataType(fieldType)) {
                value = Hash.sha3(this.encodeData(fieldType, (JSONObject) value));
                fieldType = "bytes32";
            } else if (fieldType.lastIndexOf(']') == fieldType.length() - 1) {
                throw new Exception("Arrays currently unimplemented in encodeData");
            }
            abiTypes.add(new ABITypeFactory().getType(fieldType, value));

        }
        return "0x" + FunctionEncoder.encodeConstructor(abiTypes);
    }

    private String hashData(String dataType, JSONObject data) throws Exception {
        String encodedData = this.encodeData(dataType, data);
        return Hash.sha3(encodedData);
    }

    private JSONObject getTypes() {
        //Method to get 'types' object
        try {
            return new JSONObject(this.types.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toEIP712TransactionHash() throws Exception {
        this.validate();

        String domainSeparator = this.hashData("EIP712Domain", this.domain);
        String message = this.hashData(this.primaryType, this.message);

        return new SoliditySha3().soliditySha3(
                new JSONObject(String.format("{ t: 'bytes', v: '"+ INITIAL_BYTE + "' }")),
                new JSONObject(String.format("{ t: 'bytes', v: '"+ VERSION + "' }")),
                new JSONObject(String.format("{ t: 'bytes32', v: '" + domainSeparator + "' }")),
                new JSONObject(String.format("{ t: 'bytes32', v: '" + message + "' }"))
        );
    }


    public static boolean validateData(JSONObject data) {
        boolean isDataValid = true;
        return isDataValid;
    }
    private boolean validate() throws JSONException {

        JSONObject data = new JSONObject();
        data.put("types", this.types);
        data.put("primaryType", this.primaryType);
        data.put("domain", this.domain);
        data.put("message", this.message);

        boolean isDataValid = EIP712.validateData(data);
        if (!isDataValid) {
            Error err = new Error("TypedData is invalid");
            throw err;
        }
        return isDataValid;
    }
}