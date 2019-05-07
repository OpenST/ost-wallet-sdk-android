/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.createsession;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.uicomponents.OstPrimaryEditTextView;
import ost.com.demoapp.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateSessionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateSessionFragment extends BaseFragment implements CreateSessionView {


    CreateSessionPresenter mCreateSessionPresenter = CreateSessionPresenter.getInstance();

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

        final OstPrimaryEditTextView spendingLimitEditText = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_spending_limit));
        spendingLimitEditText.setHintText(getResources().getString(R.string.create_session_spending_limit));

        final OstPrimaryEditTextView unitEditText = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_unit));
        unitEditText.setHintText(getResources().getString(R.string.create_session_unit));

        final OstPrimaryEditTextView expiryDaysEditText = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_expiry_days));
        expiryDaysEditText.setHintText(getResources().getString(R.string.create_session_expiry_days));

        unitEditText.setText(AppProvider.get().getCurrentEconomy().getTokenSymbol());

        ((Button)viewGroup.findViewById(R.id.pbtn_create_session)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spendingLimitEditText.getText().equals("") || expiryDaysEditText.getText().equals("")){
                    Toast.makeText(getContext(), "Add Mandatory Input", Toast.LENGTH_SHORT).show();
                } else {
                    mCreateSessionPresenter.createSession(
                            spendingLimitEditText.getText(),
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
        mCreateSessionPresenter.attachView(this);
        AppBar appBar = AppBar.newInstance(getContext(), "Create Session", true);
        setUpAppBar(viewGroup, appBar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCreateSessionPresenter.detachView();
    }
}