package com.ost.mobilesdk.security;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import static org.web3j.compat.Compat.UTF_8;


public class UserPassphrase {

    private final String userId;
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
        this.userId = userId;
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
