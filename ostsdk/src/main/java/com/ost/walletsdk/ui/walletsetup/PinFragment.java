/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.walletsetup;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;
import com.ost.walletsdk.ui.uicomponents.AppBar;
import com.ost.walletsdk.ui.uicomponents.OstTextView;
import com.ost.walletsdk.ui.uicomponents.PinEntryEditText;
import com.ost.walletsdk.ui.uicomponents.uiutils.Font;
import com.ost.walletsdk.ui.uicomponents.uiutils.FontFactory;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;
import com.ost.walletsdk.ui.util.KeyBoard;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends BaseFragment implements TextView.OnEditorActionListener {


    protected static final String HEADING = "heading";
    protected static final String SUB_HEADING = "sub_heading";
    private static final String SUB_HEADING_HINT = "sub_heading_hint";
    protected static final String SHOW_BACK_BUTTON = "show_back_button";
    public JSONObject contentConfig = new JSONObject();
    private String mHeading;
    private PinEntryEditText mPinEntryEditText;
    private OnFragmentInteractionListener mListener;
    private String mSubHeading;
    public boolean showTermsLine;
    private String mSubHeadingHint;
    private boolean mShowBackButton;

    public PinFragment() {
        // Required empty public constructor
    }

    public static PinFragment newInstance(String heading) {
        return newInstance(heading, null);
    }

    public static PinFragment newInstance(String heading, String subHeading) {
        return newInstance(heading, subHeading, null, true);
    }

    public static PinFragment newInstance(String heading, String subHeading, String subHeadingHint, boolean backButton) {
        PinFragment fragment = new PinFragment();
        Bundle args = new Bundle();
        args.putString(HEADING, heading);
        args.putString(SUB_HEADING, subHeading);
        args.putString(SUB_HEADING_HINT, subHeadingHint);
        args.putBoolean(SHOW_BACK_BUTTON, backButton);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHeading = getArguments().getString(HEADING);
            mSubHeading = getArguments().getString(SUB_HEADING);
            mSubHeadingHint = getArguments().getString(SUB_HEADING_HINT);
            mShowBackButton = getArguments().getBoolean(SHOW_BACK_BUTTON);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setListener(context);
    }

    protected void setListener(Context context) {
        android.support.v4.app.Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof PinFragment.OnFragmentInteractionListener) {
            mListener = (PinFragment.OnFragmentInteractionListener) parentFragment;
        } else if (context instanceof PinFragment.OnFragmentInteractionListener) {
            mListener = (PinFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(parentFragment.toString()
                    + " must implement PinFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.ost_fragment_pin, container, false);
        mPinEntryEditText = (PinEntryEditText) viewGroup.findViewById(R.id.txt_pin_entry);
        mPinEntryEditText.setOnEditorActionListener(this);

        TextView pinHeadingTextView = (TextView) viewGroup.findViewById(R.id.shtv_pin_heading);
        pinHeadingTextView.setText(StringConfig.instance(contentConfig.optJSONObject("title_label")).getString());

        TextView pinSubHeadingTextView = (TextView) viewGroup.findViewById(R.id.shtv_pin_sub_heading);
        pinSubHeadingTextView.setText(StringConfig.instance(contentConfig.optJSONObject("lead_label")).getString());

        TextView pinSubHeadingHintTextView = (TextView) viewGroup.findViewById(R.id.shtv_pin_sub_heading_hint);
        pinSubHeadingHintTextView.setText(StringConfig.instance(contentConfig.optJSONObject("info_label")).getString());

        AppBar appBar = AppBar.newInstance(getContext(), mShowBackButton);
        setUpAppBar(viewGroup, appBar);

        showTermsAndPolicyText((OstTextView) viewGroup.findViewById(R.id.pin_terms_privacy));

        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
        KeyBoard.showKeyboard(mPinEntryEditText.getContext());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        mPinEntryEditText.setError(false);
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (v.getText().toString().length() < 6) {
                mPinEntryEditText.setError(true);
            } else {
                KeyBoard.hideKeyboard(getContext());
                return onValidPin(v.getText().toString());
            }
        }
        return false;
    }

    protected boolean onValidPin(String pin) {
        mListener.onPinEntered(pin);
        return true;
    }

    public interface OnFragmentInteractionListener {
        void onPinEntered(String pin);
        void openWebView(String url);
    }

    private void showTermsAndPolicyText(OstTextView textView) {

        CharSequence linkableText = new SpannableString(StringConfig.instance(contentConfig.optJSONObject("terms_and_condition_label")).getString());

        while (true) {
            Pattern pattern = Pattern.compile("\\{\\{.+?\\}\\}");
            Matcher m = pattern.matcher(linkableText);

            if (!m.find()) {
                break;
            }

            int startIndex = m.start();
            int endIndex = m.end();
            String lookupText = linkableText.toString().substring(startIndex+2, endIndex-2);

            StringConfig lookUpTextStringConfig = StringConfig.instance(contentConfig.optJSONObject("placeholders").optJSONObject(lookupText));

            SpannableString stringToReplace = new SpannableString(
                    lookUpTextStringConfig.getString()
            );

            stringToReplace.setSpan(new ExtendedClickableSpan(mListener, lookUpTextStringConfig.getUrl()),0,stringToReplace.toString().length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringToReplace.setSpan(new ForegroundColorSpan(Color.parseColor(lookUpTextStringConfig.getColor())),0,stringToReplace.toString().length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            linkableText = TextUtils.concat(linkableText.subSequence(0, startIndex), stringToReplace, linkableText.subSequence(endIndex, linkableText.length()));
        }

        textView.setText(linkableText);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    class ExtendedClickableSpan extends ClickableSpan {

        private final String mUrlString;
        private final OnFragmentInteractionListener mListener;

        ExtendedClickableSpan(OnFragmentInteractionListener listener, String urlString) {
            mListener = listener;
            mUrlString = urlString;
        }
        @Override
        public void onClick(@NonNull View widget) {
            if (mListener != null) {
                mListener.openWebView(mUrlString);
            }
        }
    }
}