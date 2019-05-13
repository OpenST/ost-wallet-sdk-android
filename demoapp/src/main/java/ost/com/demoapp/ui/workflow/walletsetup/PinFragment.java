/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.walletsetup;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.uicomponents.OstTextView;
import ost.com.demoapp.uicomponents.PinEntryEditText;
import ost.com.demoapp.ui.BaseFragment;
import ost.com.demoapp.uicomponents.uiutils.FontCache;
import ost.com.demoapp.util.KeyBoard;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends BaseFragment implements TextView.OnEditorActionListener {


    protected static final String HEADING = "heading";
    protected static final String SUB_HEADING = "sub_heading";
    private String mHeading;
    private PinEntryEditText mPinEntryEditText;
    private OnFragmentInteractionListener mListener;
    private String mSubHeading;
    public boolean showTermsLine;

    public PinFragment() {
        // Required empty public constructor
    }

    public static PinFragment newInstance(String heading) {
        return newInstance(heading, null);
    }

    public static PinFragment newInstance(String heading, String subHeading) {
        PinFragment fragment = new PinFragment();
        Bundle args = new Bundle();
        args.putString(HEADING, heading);
        args.putString(SUB_HEADING, subHeading);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHeading = getArguments().getString(HEADING);
            mSubHeading = getArguments().getString(SUB_HEADING);
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
        } else {
            throw new RuntimeException(parentFragment.toString()
                    + " must implement PinFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_pin, container, false);
        mPinEntryEditText = (PinEntryEditText) viewGroup.findViewById(R.id.txt_pin_entry);
        mPinEntryEditText.setOnEditorActionListener(this);

        TextView pinSubHeadingTextView = (TextView) viewGroup.findViewById(R.id.shtv_pin_sub_heading);
        pinSubHeadingTextView.setText(mSubHeading);

        AppBar appBar = AppBar.newInstance(getContext(), mHeading, true);
        setUpAppBar(viewGroup, appBar);

        if(showTermsLine){
            showTermsAndPolicyText((OstTextView) viewGroup.findViewById(R.id.pin_terms_privacy));
        }

        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
        KeyBoard.showSoftKeyboard(mPinEntryEditText);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            mListener.onPinEntered(v.getText().toString());
            return true;
        }
        return false;
    }

    public interface OnFragmentInteractionListener {
        void onPinEntered(String pin);
        void openWebView(String url);
    }

    private void showTermsAndPolicyText(OstTextView textView){
        SpannableString byContinuing = new SpannableString(getResources().getString(R.string.terms_policies));
        SpannableString termsOfService = new SpannableString(getResources().getString(R.string.terms_of_service));
        ClickableSpan termsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.openWebView("https://ost.com/terms");
                }
            }
        };
        Typeface bold = FontCache.get(getActivity(), "fonts/SourceSansPro-Bold.ttf");
        int length = termsOfService.length();
        termsOfService.setSpan(termsClickableSpan,0,termsOfService.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        termsOfService.setSpan(boldSpan, 0, length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        SpannableString and = new SpannableString(getResources().getString(R.string.and));
        SpannableString policies = new SpannableString(getResources().getString(R.string.privacy_policy));
        ClickableSpan policiesClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.openWebView("https://ost.com/privacy");
                }
            }
        };
        length = policies.length();
        policies.setSpan(policiesClickableSpan,0,length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        policies.setSpan(boldSpan, 0, length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(TextUtils.concat(byContinuing,"\n",termsOfService," ", and, " ", policies));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}