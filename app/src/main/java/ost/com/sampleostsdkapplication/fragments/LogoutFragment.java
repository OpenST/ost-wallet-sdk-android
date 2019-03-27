/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.sampleostsdkapplication.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the Logout sessions screen for OstDemoApp.
 */
public class LogoutFragment extends BaseFragment {

    private static final String TAG = "LogoutFragment";
    private String mUserId;
    private OnLogoutFragmentListener mListener;

    public String getPageTitle() {
        return getResources().getString(R.string.logout_all_sessions);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = super.onCreateView(inflater, container, savedInstanceState);

        //No need of cancel and next button for logout fragment
        getNextButton().setVisibility(View.GONE);
        getCancelButton().setVisibility(View.GONE);

        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (!(activity instanceof OnLogoutFragmentListener)) {
            throw new RuntimeException(
                    "Activity using Logout Fragment does not implement OnLogoutFragmentListener"
            );
        }
        mListener = (OnLogoutFragmentListener)activity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LogoutFragment.
     */
    public static LogoutFragment newInstance(String userId) {
        LogoutFragment fragment = new LogoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mUserId = userId;
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        OstSdk.logoutAllSessions(mUserId, this);
        flowStarted();
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        super.flowComplete(ostWorkflowContext, ostContextEntity);
        mListener.relaunchApp();
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        super.flowInterrupt(ostWorkflowContext, ostError);
        mListener.relaunchApp();
    }

    public interface OnLogoutFragmentListener {
        void relaunchApp();
    }
}