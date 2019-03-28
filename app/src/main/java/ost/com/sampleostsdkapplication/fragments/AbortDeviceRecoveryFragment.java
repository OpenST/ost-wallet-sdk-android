/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;

import ost.com.sampleostsdkapplication.R;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Fragment representing the Reset User Pin screen for OstDemoApp.
 */
public class AbortDeviceRecoveryFragment extends BaseFragment {
    private String mUserId;
    private String mTokenId;
    private TextInputLayout mPassphraseTextInput;
    private EditText mEditTextPassphrase;
    private TextInputLayout mAddressToRecoverTextInput;
    private EditText mEditTextAddressToRecover;
    private LinearLayout mExternalView;
    private byte[] mAppSalt;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.two_input_fragment, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);

        ((TextView) view.findViewById(R.id.first_text_view)).setText("Input Pin");
        view.findViewById(R.id.second_text_view).setVisibility(View.GONE);

        mPassphraseTextInput = view.findViewById(R.id.first_text_input);
        mEditTextPassphrase = view.findViewById(R.id.first_edit_box);

        mAddressToRecoverTextInput = view.findViewById(R.id.second_text_input);
        mAddressToRecoverTextInput.setVisibility(View.GONE);
        return view;
    }

    public String getPageTitle() {
        return getResources().getString(R.string.abort_device_recovery);
    }

    /**
     * Perform operation on clicking next
     */
    public void onNextClick() {
        showLoader();
        String currentPin = mEditTextPassphrase.getText().toString();
        UserPassphrase passphrase = new UserPassphrase(mUserId, currentPin.getBytes(UTF_8), mAppSalt);
        OstSdk.abortDeviceRecovery(mUserId, passphrase, this);
        flowStarted();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static AbortDeviceRecoveryFragment newInstance(String tokenId, String userId, byte[] appSalt) {
        AbortDeviceRecoveryFragment fragment = new AbortDeviceRecoveryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        fragment.mAppSalt = appSalt;
        return fragment;
    }
}