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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ost.walletsdk.OstSdk;
import com.ost.sampleostsdkapplication.R;

/**
 * Fragment representing the Create user session screen for OstDemoApp.
 */
public class CreateSessionFragment extends BaseFragment {
    private String mUserId;
    private String mTokenId;
    private TextInputLayout mSpendingLimit;
    private EditText mSpendingLimitEditBox;
    private TextInputLayout mSessionExpiration;
    private EditText mSessionExpirationEditBox;
    private LinearLayout mExternalView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.create_session_fragment, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);
        mSpendingLimit = view.findViewById(R.id.spending_limit);
        mSpendingLimitEditBox = view.findViewById(R.id.spending_limit_edit_box);
        mSessionExpiration = view.findViewById(R.id.session_expiry);
        mSessionExpirationEditBox = view.findViewById(R.id.session_expiry_edit_box);
        return view;
    }

    public String getPageTitle(){
        return getResources().getString(R.string.create_session);
    }

    /**
     * Perform operation on clicking next
     */
    public void onNextClick(){
        if (TextUtils.isEmpty(mSpendingLimit.toString()) && mSpendingLimit.toString().length() < 0) {
            mSpendingLimit.setError(getResources().getString(R.string.valid_spending_limt));
            return;
        }
        if (TextUtils.isEmpty(mSessionExpirationEditBox.getText())
                ||
                (!TextUtils.isEmpty(mSessionExpirationEditBox.getText()) &&
                        Integer.parseInt(mSessionExpirationEditBox.getText().toString()) < 0)) {
            mSessionExpiration.setError(getResources().getString(R.string.valid_number_of_days));
            return;
        }

        String spendingLimit = mSpendingLimitEditBox.getText().toString();
        long expiryAfterSecs = (Integer.parseInt(mSessionExpirationEditBox.getText().toString()) * 86400);

        OstSdk.addSession(mUserId, spendingLimit, expiryAfterSecs, this);

        super.onNextClick();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static CreateSessionFragment newInstance(String tokenId, String userId) {
        CreateSessionFragment fragment = new CreateSessionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        return fragment;
    }
}
