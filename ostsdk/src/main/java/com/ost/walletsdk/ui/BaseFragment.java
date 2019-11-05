/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui;

import android.content.Context;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.uicomponents.AppBar;


/*
 * Abstract Fragment that every other Fragment in this application must implement.
 */
public abstract class BaseFragment extends Fragment implements BaseView {

    private BaseActivity baseActivity = null;
    private AppBar mAppBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup =  (ViewGroup) inflater.inflate(R.layout.ost_fragment_base, container, false);
        onCreateViewDelegate(inflater, viewGroup, savedInstanceState);
        return viewGroup;
    }

    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    protected void setUpAppBar(@NonNull ViewGroup view) {
        AppBar appBar = new AppBar(baseActivity);
        setUpAppBar(view, appBar);
    }

    protected void setUpAppBar(@NonNull ViewGroup view, @NonNull AppBar appBar) {
        ViewGroup viewGroup = view.findViewById(R.id.layout_holder);
        viewGroup.removeView(mAppBar);
        viewGroup.addView(appBar, 0 ,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mAppBar = appBar;
        mAppBar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        showAppBar(true);
    }

    protected void showAppBar(boolean show) {
        if (null != mAppBar) {
            mAppBar.setVisibility(show? View.VISIBLE: View.GONE);
        }
    }

    @Override
    public void showToastMessage(String text, Boolean isSuccess) {
        if (null != baseActivity) baseActivity.showToastMessage(text, isSuccess);
    }

    @Override
    public void showToastMessage(int textRes, Boolean isSuccess) {
        if (null != baseActivity) baseActivity.showToastMessage(textRes, isSuccess);
    }

    @Override
    public void goBack() {
        if (null != baseActivity) baseActivity.goBack();
    }

    @Override
    public void close() {
        if (null != baseActivity) baseActivity.close();
    }

    @Override
    public void showProgress(boolean show) {
        if (null != baseActivity) baseActivity.showProgress(show);
    }

    @Override
    public void showProgress(boolean show, String progressString) {
        if (null != baseActivity) baseActivity.showProgress(show, progressString);
    }

    public BaseActivity getBaseActivity() {
        return baseActivity;
    }
}