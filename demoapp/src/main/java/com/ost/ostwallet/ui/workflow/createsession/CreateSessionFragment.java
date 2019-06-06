/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.createsession;


import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.uicomponents.OstPrimaryEditTextView;
import com.ost.ostwallet.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateSessionFragment extends BaseFragment implements CreateSessionView {


    CreateSessionPresenter mCreateSessionPresenter;
    private OstPrimaryEditTextView mSpendingLimitEditText;

    public CreateSessionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static CreateSessionFragment newInstance() {
        CreateSessionFragment fragment = new CreateSessionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup =  (ViewGroup) inflater.inflate(R.layout.fragment_create_session, container, true);

        mSpendingLimitEditText = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_spending_limit));
        mSpendingLimitEditText.setHintText(getResources().getString(R.string.create_session_spending_limit));
        mSpendingLimitEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        final OstPrimaryEditTextView unitEditText = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_unit));
        unitEditText.setHintText(getResources().getString(R.string.create_session_unit));

        final OstPrimaryEditTextView expiryDaysEditText = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_expiry_days));
        expiryDaysEditText.setHintText(getResources().getString(R.string.create_session_expiry_days));
        expiryDaysEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        unitEditText.setText(AppProvider.get().getCurrentEconomy().getTokenSymbol());
        unitEditText.setEnabled(false);
        unitEditText.setInputType(InputType.TYPE_NULL);

        ((Button)viewGroup.findViewById(R.id.pbtn_create_session)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpendingLimitEditText.showErrorString(null);
                if(mSpendingLimitEditText.getText().equals("") || expiryDaysEditText.getText().equals("")){
                    Toast.makeText(getContext(), "Add Mandatory Input", Toast.LENGTH_SHORT).show();
                } else {
                    mCreateSessionPresenter.createSession(
                            mSpendingLimitEditText.getText(),
                            unitEditText.getText(),
                            expiryDaysEditText.getText()
                    );
                }
            }
        });

        ((Button)viewGroup.findViewById(R.id.linkbtn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        mCreateSessionPresenter = CreateSessionPresenter.getInstance();
        mCreateSessionPresenter.attachView(this);
        AppBar appBar = AppBar.newInstance(getContext(), "Create Session", true);
        setUpAppBar(viewGroup, appBar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCreateSessionPresenter.detachView();
        mCreateSessionPresenter = null;
    }

    @Override
    public void invalidSpendingLimit() {
        mSpendingLimitEditText.showErrorString("Invalid Spending limit");
    }
}