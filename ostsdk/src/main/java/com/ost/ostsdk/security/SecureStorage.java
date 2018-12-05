package com.ost.ostsdk.security;

public interface SecureStorage {
    /**
     * Stores a key and returns an id to retrieve the key again
     * @param identifier to store
     * @param data to store
     * @return byte array to retrieve key
     */
    byte[] encrypt(String identifier, byte[] data);

    /**
     * Retrieves a key by id from the storage
     * @param identifier to store
     * @param data to store
     * @return byte array or null if no key for this id was stored
     */
    byte[] retrieve(String identifier, byte[] data);
}
