/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.viewmnemonics;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.uicomponents.PrimaryTextView;
import ost.com.demoapp.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewMnemonicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewMnemonicsFragment extends BaseFragment implements ViewMnemonicsView {


    ViewMnemonicsPresenter mViewMnemonicsPresenter = ViewMnemonicsPresenter.getInstance();
    private PrimaryTextView mPrimaryTextView;

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

        mPrimaryTextView = viewGroup.findViewById(R.id.ptv_mnemonics);
        AppBar appBar = AppBar.newInstance(getContext(), "View Mnemonics", true);
        setUpAppBar(viewGroup, appBar);

        mViewMnemonicsPresenter.attachView(this);
    }

    @Override
    public void showMnemonics(String string) {
        mPrimaryTextView.setText(string);
    }

    @Override
    public void showError(String message) {
        mPrimaryTextView.setTextColor(Color.RED);
        mPrimaryTextView.setText(message);
    }
}