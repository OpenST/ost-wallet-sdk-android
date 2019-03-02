package com.ost.mobilesdk.security;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import static org.web3j.compat.Compat.UTF_8;


public class UserPassphrase {

    private final String userId;
    private byte[] prefixedPassphrase;

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
     * @param passphrase - Pin/Passphrase provided by the user. Min length 6.
     * @param prefix - A prefix to the Pin/Passphrase. Min length 30.
     */
    public UserPassphrase(String userId, String passphrase, String prefix) {
        this.userId = userId;
        if ( null == passphrase || passphrase.length() < OstConstants.RECOVERY_PHRASE_USER_INPUT_MIN_LENGTH ) {
            passphrase = null;
            prefix = null;
            throw new OstError("core_up_up_1", ErrorCode.INVALID_USER_PASSPHRASE);
        }

        if ( null == prefix || prefix.length() < OstConstants.RECOVERY_PHRASE_PREFIX_MIN_LENGTH) {
            passphrase = null;
            prefix = null;
            throw new OstError("core_up_up_2", ErrorCode.INVALID_PASSPHRASE_PREFIX);
        }

        this.prefixedPassphrase = String.format("%s%s%s", prefix, passphrase, userId).getBytes(UTF_8);

        //Forget the inputs.
        passphrase = null;
        prefix = null;
    }

    public String getUserId() {
        return userId;
    }

    byte[] getPrefixedPassphrase() {
        return prefixedPassphrase;
    }

    private static final byte[] nonSecret = (String.valueOf(System.currentTimeMillis()) + "RANDOM_BYTES_HERE").getBytes();
    public void clear() {
        if ( null == prefixedPassphrase ) { return; }
        for (int i = 0; i < prefixedPassphrase.length; i++) {
            prefixedPassphrase[i] = nonSecret[i % nonSecret.length];
        }
        prefixedPassphrase = null;
    }
}
