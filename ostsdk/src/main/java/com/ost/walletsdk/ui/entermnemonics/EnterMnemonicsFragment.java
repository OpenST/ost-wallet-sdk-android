/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.entermnemonics;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.ui.sdkInteract.WorkFlowListener;
import com.ost.walletsdk.ui.uicomponents.AppBar;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;

import org.json.JSONObject;

import static com.ost.walletsdk.ui.workflow.OstWorkFlowActivity.USER_ID;
import static com.ost.walletsdk.ui.workflow.OstWorkFlowActivity.WORKFLOW_ID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterMnemonicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterMnemonicsFragment extends BaseFragment implements EnterMnemonicsView {


    EnterMnemonicsPresenter mEnterMnemonicsPresenter = EnterMnemonicsPresenter.getInstance();

    JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("add_current_device_with_mnemonics").optJSONObject("provide_mnemonics");
    public EnterMnemonicsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static EnterMnemonicsFragment newInstance(Bundle bundle) {
        EnterMnemonicsFragment fragment = new EnterMnemonicsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String userId = getArguments().getString(USER_ID);
            String workflowId = getArguments().getString(WORKFLOW_ID);
            WorkFlowListener workFlowListener = SdkInteract.getInstance().getWorkFlowListener(workflowId);
            mEnterMnemonicsPresenter.setArguments(userId, workFlowListener);
        }
    }

    @Override
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup =  (ViewGroup) inflater.inflate(R.layout.ost_fragment_enter_mnemonics, container, true);

        TextView headingView = viewGroup.findViewById(R.id.labelHeading);
        headingView.setText(
                StringConfig.instance(contentConfig.optJSONObject("title_label")).getString()
        );

        TextView subHeadingView = viewGroup.findViewById(R.id.labelSubHeading);
        subHeadingView.setText(
                StringConfig.instance(contentConfig.optJSONObject("info_label")).getString()
        );

        TextView hintView = viewGroup.findViewById(R.id.labelHint);
        hintView.setText(
                StringConfig.instance(contentConfig.optJSONObject("bottom_label")).getString()
        );

        Button recoverButton = viewGroup.findViewById(R.id.pbtn_recover_wallet);
        recoverButton.setText(
                StringConfig.instance(contentConfig.optJSONObject("action_button")).getString()
        );

        final EditText mnemonicsPhrase = ((EditText)viewGroup.findViewById(R.id.et_mnemonics_phrase));

        mnemonicsPhrase.setHint(
                StringConfig.instance(contentConfig.optJSONObject("placeholder")).getString()
        );

        recoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnterMnemonicsPresenter.recoverWallet(mnemonicsPhrase.getText().toString());
            }
        });

        mEnterMnemonicsPresenter.attachView(this);
        AppBar appBar = AppBar.newInstance(getContext(), false);
        setUpAppBar(viewGroup, appBar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEnterMnemonicsPresenter.detachView();
        mEnterMnemonicsPresenter = null;
    }
}