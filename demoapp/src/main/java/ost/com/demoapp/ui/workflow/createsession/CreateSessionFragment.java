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

import ost.com.demoapp.R;
import ost.com.demoapp.customView.AppBar;
import ost.com.demoapp.customView.PrimaryEditTextView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup =  (ViewGroup) inflater.inflate(R.layout.fragment_create_session, container, false);

        final PrimaryEditTextView spendingLimitEditText = ((PrimaryEditTextView)viewGroup.findViewById(R.id.etv_spending_limit));
        final PrimaryEditTextView unitEditText = ((PrimaryEditTextView)viewGroup.findViewById(R.id.etv_unit));
        final PrimaryEditTextView expiryDaysEditText = ((PrimaryEditTextView)viewGroup.findViewById(R.id.etv_expiry_days));

        ((Button)viewGroup.findViewById(R.id.pbtn_create_session)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreateSessionPresenter.createSession(
                        spendingLimitEditText.getText(),
                        unitEditText.getText(),
                        expiryDaysEditText.getText()
                );
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

        return viewGroup;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCreateSessionPresenter.detachView();
    }
}