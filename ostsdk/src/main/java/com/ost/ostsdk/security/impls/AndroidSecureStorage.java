package com.ost.ostsdk.security.impls;

import com.ost.ostsdk.security.SecureStorage;

public class AndroidSecureStorage implements SecureStorage {
    @Override
    public byte[] encrypt(String identifier, byte[] data) {
        return new byte[0];
    }

    @Override
    public byte[] retrieve(String identifier, byte[] data) {
        return new byte[0];
    }
}
