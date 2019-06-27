/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.viewmnemonics;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ost.ostwallet.R;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.uicomponents.OstTextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewMnemonicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewMnemonicsFragment extends BaseFragment implements ViewMnemonicsView {


    ViewMnemonicsPresenter mViewMnemonicsPresenter = ViewMnemonicsPresenter.getInstance();
    private OstTextView mOstTextView1;
    private OstTextView mOstTextView2;

    public ViewMnemonicsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static ViewMnemonicsFragment newInstance() {
        ViewMnemonicsFragment fragment = new ViewMnemonicsFragment();
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
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_view_mnemonics, container, true);

        mOstTextView1 = viewGroup.findViewById(R.id.mnemonics_tv_1);
        mOstTextView2 = viewGroup.findViewById(R.id.mnemonics_tv_2);
        AppBar appBar = AppBar.newInstance(getContext(), "View Mnemonics", true);
        setUpAppBar(viewGroup, appBar);

        mViewMnemonicsPresenter.attachView(this);
    }

    @Override
    public void showMnemonics(String string) {
        String[] arr = string.split(" ");
        String str1 = "";
        String str2 = "";
        for(int i=0; i <arr.length; i++){
            if(i<6){
                str1 += String.format("%s. %s\n\n", i+1, arr[i]);
            } else {
                str2 += String.format("%s. %s\n\n", i+1, arr[i]);
            }
        }
        mOstTextView1.setText(str1);
        mOstTextView2.setText(str2);
    }

    @Override
    public void showError(String message) {

    }
}