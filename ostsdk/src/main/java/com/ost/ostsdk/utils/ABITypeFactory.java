package com.ost.ostsdk.utils;

import android.util.Log;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.AbiTypes;
import org.web3j.abi.datatypes.generated.Uint160;
import org.web3j.utils.Numeric;

import java.lang.reflect.Constructor;
import java.math.BigInteger;

class ABITypeFactory {

    public ABITypeFactory() {

    }

    Type getType(String type, Object value) {
        Class cls = AbiTypes.getType(type);
        Constructor<?> constructor;
        Type object = null;
        try {
            if (cls == Address.class) {
                if (value instanceof Uint160) {
                    constructor = cls.getConstructor(Uint160.class);
                    object = (Type) constructor.newInstance(new Object[]{value});
                } else if (value instanceof BigInteger) {
                    constructor = cls.getConstructor(BigInteger.class);
                    object = (Type) constructor.newInstance(new Object[]{value});
                } else if (value instanceof String) {
                    constructor = cls.getConstructor(String.class);
                    object = (Type) constructor.newInstance(new Object[]{value});
                }
            } else if (cls == Utf8String.class) {
                constructor = cls.getConstructor(String.class);
                object = (Type) constructor.newInstance(new Object[]{value});
            } else if (cls == Bool.class) {
                constructor = cls.getConstructor(Boolean.class);
                object = (Type) constructor.newInstance(new Object[]{value});
            } else if (type.startsWith("bytes")) {
                constructor = cls.getConstructor(byte[].class);
                object = (Type)constructor.newInstance(new Object[]{Numeric.hexStringToByteArray((String)value)});
            } else if (type.startsWith("uint") || type.startsWith("int")){
                if (value instanceof BigInteger) {
                    constructor = cls.getConstructor(BigInteger.class);
                    object = (Type) constructor.newInstance(new Object[]{value});
                } else {
                    constructor = cls.getConstructor(long.class);
                    object = (Type)constructor.newInstance(new Object[]{value});
                }
            } else {
                throw new Exception("Unknown type encountered");
            }
        } catch (Exception exception) {
            Log.e("ABITypeFactory", exception.getMessage());
        }

        return object;
    }
}