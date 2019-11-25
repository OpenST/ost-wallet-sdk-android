/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.utils;

import com.ost.walletsdk.annotations.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoliditySha3 {

    private static final String TAG = SoliditySha3.class.getName();

    public SoliditySha3() {
    }

    public String soliditySha3(Object... ObjectList) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object obj : ObjectList) {
            String hexValue = processSoliditySha3Args(obj);
            Log.d(TAG, String.format("Hex value of %s is %s", obj.toString(), hexValue));

            stringBuilder.append(hexValue);
        }
        Log.d(TAG, String.format("Message before hash %s", stringBuilder.toString()));

        return Hash.sha3("0x" + stringBuilder.toString());
    }

    private String processSoliditySha3Args(Object obj) throws Exception {
        String type = null;
        Object value = null;
        int arraySize = 0;

        if (obj instanceof List) {
            List list = (List) obj;
            if (list.size() >= 2) {
                type = (String) list.get(0);
                value = list.get(1);
            }

        } else if (obj instanceof Map) {
            Map map = (Map) obj;
            if (map.size() > 0) {
                Iterator iterator = map.keySet().iterator();
                type = (String) iterator.next();
                value = map.get(type);
            }
        } else if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has("v") || jsonObject.has("t") || jsonObject.has("value") || jsonObject.has("type")) {
                type = jsonObject.optString("t", jsonObject.optString("type"));
                value = jsonObject.opt("v");
                if (null == value) {
                    value = jsonObject.opt("value");
                }
                if (null == value) {
                    throw new Exception("Value is null");
                }
            } else {
                // Else case doesn't exist because json object will surely be their
            }
        } else if (obj instanceof String) {
            if (Numeric.containsHexPrefix((String) obj)) {
                return Numeric.cleanHexPrefix((String) obj);
            }
            return Numeric.cleanHexPrefix(Numeric.toHexString(((String) obj).getBytes()));
        } else {
            throw new Exception("Unknown obj type received");
        }


        if ((type.startsWith("int") || type.startsWith("uint")) && value instanceof String && !((String) value).matches("^(-)?0x")) {
            value = new BigInteger((String) value);
        }

        if (value instanceof JSONArray) {
            arraySize = ((JSONArray) value).length();
        }

        if (value instanceof JSONArray) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < ((JSONArray) value).length(); i++) {
                Object innerValue = ((JSONArray) value).get(i);
                String hexValue = solidityPack(type, innerValue, arraySize);
                stringBuilder.append(hexValue.replace("0x", ""));
            }
            return stringBuilder.toString();
        } else {
            String hexArg = solidityPack(type, value, arraySize);
            return hexArg.replace("0x", "");
        }

    }

    private String solidityPack(@NonNull String type, @NonNull Object value, int arraySize) throws Exception {
        int size;
        BigInteger num;
        type = elementaryName(type).toLowerCase();
        if ("bytes".equals(type)) {
            if (((String) value).length() % 2 != 0) {
                throw new Exception("Invalid bytes character length " + ((String) value).length());
            }
            return (String) value;
        } else if ("string".equals(type)) {
            return Numeric.toHexString(((String) value).getBytes());
        } else if ("bool".equals(type)) {
            return (Boolean) value ? "01" : "00";
        } else if (type.startsWith("address")) {
            if (arraySize != 0) {
                size = 64;
            } else {
                size = 40;
            }
            value = ((String) value).toLowerCase();

            if (!isAddress((String) value)) {
                throw new Exception(value + " is not a valid address, or the checksum is invalid.");
            }

            return leftPad((String) value, size);
        }
        size = parseTypeN(type);

        if (type.startsWith("bytes")) {

            if (size == -1) {
                throw new Exception("bytes[] not yet supported in solidity");
            }

            // must be 32 byte slices when in an array
            if (arraySize != 0) {
                size = 32;
            }

            if (size < 1 || size > 32 || size < Numeric.cleanHexPrefix((String) value).length() / 2) {
                throw new Error("Invalid bytes" + size + " for  " + value);
            }

            return rightPad((String) value, size * 2);
        } else if (type.startsWith("uint")) {

            if ((size % 8 != 0) || (size < 8) || (size > 256)) {
                throw new Error("Invalid uint " + size + " size");
            }

            num = parseNumber(value);
            if (num.bitLength() > size) {
                throw new Error("Supplied uint exceeds width: " + size + " vs " + num.bitLength());
            }

            if (num.compareTo(new BigInteger("0")) < 0) {
                throw new Error("Supplied uint " + num.toString() + " is negative");
            }

            return size != -1 ? leftPad(num.toString(16), size / 8 * 2) : num.toString(16);
        } else if (type.startsWith("int")) {

            if ((size % 8 != 0) || (size < 8) || (size > 256)) {
                throw new Error("Invalid uint " + size + " size");
            }

            num = parseNumber(value);
            if (num.bitLength() > size) {
                throw new Error("Supplied int exceeds width: " + size + " vs " + num.bitLength());
            }

            if (num.compareTo(new BigInteger("0")) < 0) {
                return num.abs().toString(16);
            } else {
                return size != -1 ? leftPad(num.toString(16), size / 8 * 2) : num.toString(16);
            }

        } else {
            // FIXME: support all other types
            throw new Error("Unsupported or invalid type: " + type);
        }
    }

    private BigInteger twosComplement(BigInteger original) {
        // for negative BigInteger, top byte is negative
        byte[] contents = original.toByteArray();

        // prepend byte of opposite sign
        byte[] result = new byte[contents.length + 1];
        System.arraycopy(contents, 0, result, 1, contents.length);
        result[0] = (contents[0] < 0) ? 0 : (byte) -1;

        // this will be two's complement
        return new BigInteger(result);
    }

    private BigInteger parseNumber(Object value) {
        if (value instanceof String) {
            if (Numeric.containsHexPrefix((String) value)) {
                return new BigInteger(Numeric.cleanHexPrefix((String) value), 16);
            } else {
                return new BigInteger((String) value, 10);
            }
        } else if (value instanceof BigInteger) {
            return (BigInteger) value;
        } else
            throw new Error(value + " is not a number");
    }

    private int parseTypeN(String type) {
        boolean matches = type.matches("^\\D+(\\d+).*$");
        if (matches) {
            Matcher matcher = Pattern.compile("^\\D+(\\d+).*$").matcher(type);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1), 10);
            }
        }
        return -1;
    }


    // Parse N from type[<N>]
    private int parseTypeNArray(String type) {
        boolean matches = type.matches("^\\D+\\d*\\[(\\d+)\\]$");
        if (matches) {
            Matcher matcher = Pattern.compile("^\\D+\\d*\\[(\\d+)\\]$").matcher(type);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1), 10);
            }
        }
        return -1;
    }

    private String leftPad(String string, int size) {
        boolean hasPrefix = Numeric.containsHexPrefix(string);
        string = Numeric.cleanHexPrefix(string);

        return String.format(String.format(Locale.getDefault(), "%%%ds", size), string).replace(' ', '0');
    }

    private String rightPad(String string, int size) {
        boolean hasPrefix = Numeric.containsHexPrefix(string);
        string = Numeric.cleanHexPrefix(string);

        return String.format(String.format(Locale.getDefault(), "%%-%ds", size), string).replace(' ', '0');
    }

    private boolean isAddress(String address) {
        // check if it has the basic requirements of an address
        if (!address.matches("^(0x)?[0-9a-f]{40}$")) {
            return false;
            // If it's ALL lowercase or ALL upppercase
        } else if (address.matches("^(0x|0X)?[0-9a-f]{40}$") || address.matches("^(0x|0X)?[0-9A-F]{40}$")) {
            return true;
            // Otherwise check each case
        } else {
            return checkAddressChecksum(address);
        }
    }

    private boolean checkAddressChecksum(String address) {
        // Check each case
        address = Numeric.cleanHexPrefix(address);
        String addressHash = Numeric.cleanHexPrefix(Hash.sha3(address.toLowerCase()));

        for (int i = 0; i < 40; i++) {
            // the nth letter should be uppercase if the nth digit of casemap is 1
            if ((Integer.parseInt(addressHash.substring(i, i + 1), 16) > 7 &&
                    !address.substring(i, i + 1).toUpperCase().equals(address.substring(i, i + 1))) ||
                    (Integer.parseInt(addressHash.substring(i, i + 1), 16) <= 7 &&
                            !address.substring(i, i + 1).toLowerCase().equals(address.substring(i, i + 1)))) {
                return false;
            }
        }
        return true;
    }

    private String elementaryName(String name) {
        if (name.startsWith("int[")) {
            return "int256" + name.substring(3);
        } else if ("int".equals(name)) {
            return "int256";
        } else if (name.startsWith("uint[")) {
            return "uint256" + name.substring(4);
        } else if ("uint".equals(name)) {
            return "uint256";
        } else if (name.startsWith("fixed[")) {
            return "fixed128x128" + name.substring(5);
        } else if ("fixed".equals(name)) {
            return "fixed128x128";
        } else if (name.startsWith("ufixed[")) {
            return "ufixed128x128" + name.substring(6);
        } else if ("ufixed".equals(name)) {
            return "ufixed128x128";
        }
        return name;
    }
}