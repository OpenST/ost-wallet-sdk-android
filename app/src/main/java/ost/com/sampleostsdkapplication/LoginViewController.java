/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.sampleostsdkapplication;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class LoginViewController {
    private static final String TAG = "LoginViewController";

    private static final String REGISTER_TYPE = "register_type";
    private static final String OST_USER_ID = "ost_user_id";

    private final LoginFragmentInterface mLoginFragment;

    public LoginViewController(LoginFragmentInterface loginFragment) {
        mLoginFragment = loginFragment;
    }

    public void onButtonAction(Editable userName, Editable mobileNumber, final boolean isRegister) {
        boolean success = true;
        if (!isPasswordValid(mobileNumber)) {
            mLoginFragment.setMobileNumberError(getString(R.string.error_incorrect_mobilenumber));
            success = false;
        }
        if (!isUserNameValid(userName)) {
            mLoginFragment.setUserNameError(getString(R.string.error_incorrect_name));
            success = false;
        }
        if (success) {
            mLoginFragment.resetNumberError();
            mLoginFragment.resetUserNameError();
            mLoginFragment.showProgress(true);
            if (isRegister) {
                new MappyApiClient().createUser(userName.toString(), mobileNumber.toString(), "description", new MappyApiClient.Callback() {
                    @Override
                    public void onResponse(boolean success, JSONObject response) {
                        Log.i(TAG, "On User Register response");
                        parseResponse(response, isRegister);
                    }
                });
            } else {
                new MappyApiClient().loginUser(userName.toString(), mobileNumber.toString(), new MappyApiClient.Callback() {
                    @Override
                    public void onResponse(boolean success, JSONObject response) {
                        Log.i(TAG, "On User Login response");
                        parseResponse(response, isRegister);
                    }
                });
            }
        }
    }

    private void parseResponse(JSONObject response, boolean isRegister) {
        if (null != response && null != mLoginFragment.getActivity()) {
            Activity activity = mLoginFragment.getActivity();
            Toast.makeText(activity.getApplicationContext(), isRegister ? "User Registered" : "User Authenticated", Toast.LENGTH_SHORT).show();
            Log.i(TAG, String.format("JSON Response : %s", response.toString()));

            LogInUser logInUser = new LogInUser(response);
            ((App) activity.getApplicationContext()).setLoggedUser(logInUser);
            String userId = logInUser.getOstUserId();
            String tokenId = logInUser.getTokenId();
            if (null != userId) {
                mLoginFragment.showProgress(false);

                mLoginFragment.startUserListActivity();
                return;
            }
            Log.i(TAG, "UserId is null");
        }
        mLoginFragment.showProgress(false);
        mLoginFragment.setUserNameError(getString(R.string.error_incorrect_name));
        mLoginFragment.setMobileNumberError(getString(R.string.error_incorrect_mobilenumber));
    }

    private String getString(int resId) {
        return mLoginFragment.getString(resId);
    }

    private boolean isUserNameValid(Editable text) {
        return !TextUtils.isEmpty(text);
    }

    /*
       In reality, this will have more complex logic including, but not limited to, actual
       authentication of the username and password.
    */
    private boolean isPasswordValid(@Nullable Editable text) {
        return !TextUtils.isEmpty(text) && text.length() == 10;
    }

    public void onNumberListner(Editable text) {
        if (isPasswordValid(text)) {
            mLoginFragment.resetNumberError();
        }
    }

    public void onDestroy() {

    }

    public interface LoginFragmentInterface {
        void setMobileNumberError(String text);

        void setUserNameError(String text);

        void resetNumberError();

        void resetUserNameError();

        void showProgress(boolean show);

        Activity getActivity();

        String getString(int text);

        void startUserListActivity();
    }
}