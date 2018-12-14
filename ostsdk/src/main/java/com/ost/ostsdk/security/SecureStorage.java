package com.ost.ostsdk.security;

public interface SecureStorage {
    /**
     * generate new keys for provided identifier as key alias
     * @param identifier for key alias
     */
    void addKeyIdentifier(String identifier);

    /**
     * delete keys for provided identifier as key alias
     *
     * @param identifier for key alias
     */
    void removeIdentifier(String identifier);

    /**
     * Stores a data and returns an id to retrieve the key again
     * @param identifier for key alias
     * @param data to store
     * @return byte array to retrieve key
     */
    byte[] encrypt(String identifier, byte[] data);

    /**
     * Retrieves a key by id from the storage
     * @param identifier for key alias
     * @param data to store
     * @return byte array or null if no key for this id was stored
     */
    byte[] retrieve(String identifier, byte[] data);
}
