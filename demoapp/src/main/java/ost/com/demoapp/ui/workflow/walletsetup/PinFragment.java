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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.uicomponents.PinEntryEditText;
import ost.com.demoapp.ui.BaseFragment;
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
    }
}