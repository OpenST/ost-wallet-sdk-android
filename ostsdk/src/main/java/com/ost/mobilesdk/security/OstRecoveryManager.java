package com.ost.mobilesdk.security;

import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static org.web3j.compat.Compat.UTF_8;

public class OstRecoveryManager {
    private static String TAG = "OstRecoveryManager";
    private static final String SALT = "salt";
    private static final String SCRYPT_SALT = "scrypt_salt";

    private String userId;
    private OstApiClient apiClient;
    public OstRecoveryManager(String userId) {
        this.userId = userId;
        apiClient = new OstApiClient(userId);
    }

    private OstUser ostUser() {
        return OstUser.getById(userId);
    }

    public String getRecoveryAddressFor(UserPassphrase passphrase) {
        InternalKeyManager2 ikm = null;
        try {
            if ( ostUser().isActivated() ) {
                throw new OstError("km_orm_gra_1", ErrorCode.USER_ALREADY_ACTIVATED);
            } else if ( ostUser().isActivating() ) {
                throw new OstError("km_orm_gra_2", ErrorCode.USER_ACTIVATING);
            }

            ikm = new InternalKeyManager2(userId);
            return ikm.getRecoveryAddress(passphrase, getSalt());
        } finally {
            ikm = null;
        }
    }

    public boolean validatePassphrase(UserPassphrase passphrase) {
        InternalKeyManager2 ikm = null;
        try {
            if ( !ostUser().isActivated() ) {
                throw new OstError("km_orm_vp_1", ErrorCode.USER_NOT_ACTIVATED);
            }

            ikm = new InternalKeyManager2(userId);
            if ( !ikm.isUserPassphraseValidationAllowed() ) {
                throw new OstError("km_orm_vp_2", ErrorCode.USER_PASSPHRASE_VALIDATION_LOCKED);
            }

            return ikm.validateUserPassphrase(passphrase, getSalt());
        } finally {
            ikm = null;
        }
    }


    //region - SCrypt salt from Kit.
    private byte[] getSalt() {
        JSONObject jsonObject = null;
        JSONObject jsonData = null;
        JSONObject jsonSalt = null;
        try {
            jsonObject = apiClient.getSalt();
            jsonData = jsonObject.getJSONObject(OstConstants.RESPONSE_DATA);
            jsonSalt = jsonData.getJSONObject(SALT);
            return jsonSalt.getString(SCRYPT_SALT).getBytes(UTF_8);
        } catch (IOException e) {
            throw new OstError("km_rm_gs_1", ErrorCode.SALT_API_FAILED);
        } catch (JSONException e) {
            throw new OstError("km_rm_gs_2", ErrorCode.SALT_API_FAILED);
        } catch (Throwable th) {
            //Catch everything esle.
            throw new OstError("km_rm_gs_3", ErrorCode.SALT_API_FAILED);
        }
        finally {
            if ( null != jsonSalt && jsonSalt.has(SCRYPT_SALT) ) {
                jsonSalt.remove(SCRYPT_SALT);
            }
        }
    }
    //endregion


}
