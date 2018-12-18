package com.ost.ostsdk.security;

public interface SecureStorage {

    /**
     * Stores a data and returns an id to decrypt the key again
     * @param data to store
     * @return byte array to decrypt key
     */
    byte[] encrypt(byte[] data);

    /**
     * Retrieves a key by id from the storage
     * @param data to decrypt
     * @return byte array or null if no key for this id was stored
     */
    byte[] decrypt(byte[] data);
}
