/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;

import com.ost.sampleostsdkapplication.R;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Fragment representing the Reset User Pin screen for OstDemoApp.
 */
public class ResetPinFragment extends BaseFragment {
    private static final String TAG = "ResetPinFragment";
    private String mUserId;
    private String mTokenId;
    private TextInputLayout mOldPinTextInput;
    private EditText mOldPinEditBox;
    private TextInputLayout mNewPinTextInput;
    private EditText mNewPinEditBox;
    private byte[] mAppSalt;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.two_input_fragment, null);
        LinearLayout mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);
        mOldPinTextInput = view.findViewById(R.id.first_text_input);
        mOldPinEditBox = view.findViewById(R.id.first_edit_box);
        mNewPinTextInput = view.findViewById(R.id.second_text_input);
        mNewPinEditBox = view.findViewById(R.id.second_edit_box);
        return view;
    }

    public String getPageTitle(){
        return getResources().getString(R.string.reset_pin);
    }

    /**
     * Perform operation on clicking next
     */
    public void onNextClick(){
        if (mOldPinEditBox.getText() == null || mOldPinEditBox.getText().length() < 6){
            mOldPinTextInput.setError(getResources().getString(R.string.enter_six_digit_pin));
            return;
        }
        if (mNewPinEditBox.getText() == null || mNewPinEditBox.getText().length() < 6){
            mNewPinTextInput.setError(getResources().getString(R.string.enter_six_digit_pin));
            return;
        }
        if (mNewPinEditBox.getText().toString().equals(mOldPinEditBox.getText().toString())){
            mNewPinTextInput.setError(getResources().getString(R.string.new_old_pin_same));
            return;
        }
        showLoader();

        Log.d(TAG, "Start Reset pin process");
        String oldPin = mOldPinEditBox.getText().toString();
        String newPin = mNewPinEditBox.getText().toString();
        UserPassphrase currentPassphrase = new UserPassphrase(mUserId, oldPin.getBytes(UTF_8), mAppSalt.clone());
        UserPassphrase newPassphrase = new UserPassphrase(mUserId, newPin.getBytes(UTF_8), mAppSalt.clone());

        OstSdk.resetPin(mUserId, currentPassphrase, newPassphrase, this);

        super.onNextClick();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static ResetPinFragment newInstance(String tokenId, String userId, byte[] appSalt) {
        ResetPinFragment fragment = new ResetPinFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        fragment.mAppSalt = appSalt;
        return fragment;
    }
}
