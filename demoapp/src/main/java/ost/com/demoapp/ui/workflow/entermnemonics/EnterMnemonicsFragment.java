/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.entermnemonics;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterMnemonicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterMnemonicsFragment extends BaseFragment implements EnterMnemonicsView {


    EnterMnemonicsPresenter mEnterMnemonicsPresenter;

    public EnterMnemonicsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static EnterMnemonicsFragment newInstance() {
        EnterMnemonicsFragment fragment = new EnterMnemonicsFragment();
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
        ViewGroup viewGroup =  (ViewGroup) inflater.inflate(R.layout.fragment_enter_mnemonics, container, true);

        final EditText mnemonicsPhrase = ((EditText)viewGroup.findViewById(R.id.et_mnemonics_phrase));

        ((Button)viewGroup.findViewById(R.id.pbtn_recover_wallet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnterMnemonicsPresenter.recoverWallet(mnemonicsPhrase.getText().toString());
            }
        });

        mEnterMnemonicsPresenter = EnterMnemonicsPresenter.getInstance();
        mEnterMnemonicsPresenter.attachView(this);
        AppBar appBar = AppBar.newInstance(getContext(), "Enter 12-word Mnemonic", true);
        setUpAppBar(viewGroup, appBar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEnterMnemonicsPresenter.detachView();
        mEnterMnemonicsPresenter = null;
    }
}