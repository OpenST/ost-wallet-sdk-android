/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ecKeyInteracts;

public interface OstSecureStorage {

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
