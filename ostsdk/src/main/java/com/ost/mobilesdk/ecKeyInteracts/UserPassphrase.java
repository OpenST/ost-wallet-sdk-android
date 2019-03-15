/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.mobilesdk.ecKeyInteracts;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import static org.web3j.compat.Compat.UTF_8;


public class UserPassphrase {

    private String userId;
    private byte[] passphrase;
    private boolean wiped = false;

    /**
     *
     * A simple class that is used to transport user passphrase through various layers of app and Sdk.
     * Note: Information contained in UserPassphrase shall be wiped out after use by Sdk.
     * Do not retain it. It can not be used more than once.
     *
     * Note: It is developer's responsibility to provide correct prefix.
     * It is important that each user has a different prefix.
     * To generate random strings for each user, you may use BIP-39.
     * Be sure to choose the library carefully.
     *
     * @param userId - Id of the user - as provided by https://kit.ost.com
     * @param userPin - Pin/Passphrase provided by the user. Min length 6.
     * @param passphrasePrefix - A prefix to the Pin/Passphrase. Min length 30.
     */
    public UserPassphrase(String userId, String userPin, String passphrasePrefix) {
        setUserId(userId);
        if ( null == userPin || userPin.length() < OstConstants.RECOVERY_PHRASE_USER_INPUT_MIN_LENGTH ) {
            userPin = null;
            passphrasePrefix = null;
            throw new OstError("core_up_up_1", ErrorCode.INVALID_USER_PASSPHRASE);
        }

        if ( null == passphrasePrefix || passphrasePrefix.length() < OstConstants.RECOVERY_PHRASE_PREFIX_MIN_LENGTH) {
            userPin = null;
            passphrasePrefix = null;
            throw new OstError("core_up_up_2", ErrorCode.INVALID_PASSPHRASE_PREFIX);
        }

        this.passphrase = String.format("%s%s%s", passphrasePrefix, userPin, userId).getBytes(UTF_8);

        //Forget the inputs.
        userPin = null;
        passphrasePrefix = null;
    }

    public UserPassphrase(String userId, byte[] userPin, byte[] passphrasePrefix) {
        setUserId(userId);
        if ( null == userPin || userPin.length < OstConstants.RECOVERY_PHRASE_USER_INPUT_MIN_LENGTH ) {
            userPin = null;
            passphrasePrefix = null;
            throw new OstError("core_up_up2_1", ErrorCode.INVALID_USER_PASSPHRASE);
        }

        if ( null == passphrasePrefix || passphrasePrefix.length < OstConstants.RECOVERY_PHRASE_PREFIX_MIN_LENGTH) {
            userPin = null;
            passphrasePrefix = null;
            throw new OstError("core_up_up2_2", ErrorCode.INVALID_PASSPHRASE_PREFIX);
        }

        int passphraseLength = passphrasePrefix.length + userPin.length + userId.length();

        byte[] userIdBytes = userId.getBytes(UTF_8);
        passphrase = new byte[passphraseLength];

        int cpyPos = 0;
        int cpyLen = passphrasePrefix.length;
        System.arraycopy(passphrasePrefix, 0, passphrase, cpyPos, cpyLen );

        cpyPos += cpyLen;
        cpyLen = userPin.length;
        System.arraycopy(userPin, 0, passphrase, cpyPos, cpyLen );

        cpyPos += cpyLen;
        cpyLen = userIdBytes.length;
        System.arraycopy(userIdBytes, 0, passphrase, cpyPos, cpyLen );

        //Forget the inputs.
        userPin = null;
        passphrasePrefix = null;
    }

    private void setUserId(String userId) {
        OstUser ostUser = OstUser.getById(userId);
        if ( null == ostUser ) {
            throw new OstError("core_up_sui_1", ErrorCode.DEVICE_NOT_SETUP);
        }
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    byte[] getPassphrase() {
        return passphrase;
    }

    /**
     *
     * @return true if passphrase has been wiped off memory.
     */
    public boolean isWiped() { return wiped; }


    /**
     * Method that shall be used to wipe passphrase.
     */
    public void wipe() {
        if ( null == passphrase) { return; }
        for (int i = 0; i < passphrase.length; i++) {
            passphrase[i] = nonSecret[i % nonSecret.length];
        }
        passphrase = null;
        wiped = true;
    }
    private static final byte[] nonSecret = (String.valueOf(System.currentTimeMillis()) + "RANDOM_BYTES_HERE").getBytes();

}
