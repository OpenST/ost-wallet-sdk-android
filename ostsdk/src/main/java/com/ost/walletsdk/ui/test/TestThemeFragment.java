/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.test;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestThemeFragment extends BaseFragment {

    public TestThemeFragment() {
        // Required empty public constructor
    }

    public static TestThemeFragment newInstance() {
        TestThemeFragment fragment = new TestThemeFragment();
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.ost_test_theme_fragment, container, false);
        setUpAppBar(viewGroup);

        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}