/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.viewmnemonics;


import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;
import com.ost.walletsdk.ui.uicomponents.AppBar;
import com.ost.walletsdk.ui.uicomponents.OstH2Label;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewMnemonicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewMnemonicsFragment extends BaseFragment {

    private OstH2Label mOstTextView1;
    private OstH2Label mOstTextView2;
    private String mnemonics;
    private JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("view_mnemonics").optJSONObject("show_mnemonics");
    public ViewMnemonicsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     * @param mnemonics mnemonics
     */
    public static ViewMnemonicsFragment newInstance(String mnemonics) {
        ViewMnemonicsFragment fragment = new ViewMnemonicsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mnemonics = mnemonics;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.ost_fragment_view_mnemonics, container, true);

        mOstTextView1 = viewGroup.findViewById(R.id.mnemonics_tv_1);
        mOstTextView1.setGravity(Gravity.START);
        mOstTextView2 = viewGroup.findViewById(R.id.mnemonics_tv_2);
        mOstTextView2.setGravity(Gravity.START);

        TextView labelHeading = viewGroup.findViewById(R.id.labelHeading);
        labelHeading.setText(
                StringConfig.instance(contentConfig.optJSONObject("title_label")).getString()
        );

        TextView labelSubHeading = viewGroup.findViewById(R.id.labelSubHeading);
        labelSubHeading.setText(
                StringConfig.instance(contentConfig.optJSONObject("lead_label")).getString()
        );

        TextView labelCaution = viewGroup.findViewById(R.id.labelCaution);
        labelCaution.setText(
                StringConfig.instance(contentConfig.optJSONObject("terms_and_condition_label")).getString()
        );

        AppBar appBar = AppBar.newInstance(getContext(), false);
        setUpAppBar(viewGroup, appBar);
        showMnemonics(mnemonics);
    }


    private void showMnemonics(String string) {
        String[] arr = string.split(" ");
        String str1 = "";
        String str2 = "";
        for(int i=0; i <arr.length; i++){
            if(i<6){
                if (i == 5) {
                    str1 += String.format("%s. %s", i + 1, arr[i]);
                } else {
                    str1 += String.format("%s. %s\n\n", i + 1, arr[i]);
                }
            } else {
                if (i == 11) {
                    str2 += String.format("%s. %s", i + 1, arr[i]);
                } else {
                    str2 += String.format("%s. %s\n\n", i + 1, arr[i]);
                }
            }
        }
        mOstTextView1.setText(str1);
        mOstTextView2.setText(str2);
    }

    public void showError(String message) {

    }
}