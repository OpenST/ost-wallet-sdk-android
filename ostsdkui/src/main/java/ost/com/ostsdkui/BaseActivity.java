/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui;


import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import ost.com.ostsdkui.util.DialogFactory;


public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    private boolean isRestored;
    private ProgressDialog progressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isRestored = savedInstanceState != null;
        setupOrientation();

        //Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        super.onCreate(savedInstanceState);
    }

    private void setupOrientation() {
        if (isPortraitOnly()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showToastMessage(String text, Boolean isSuccess) {
        showSnackBar(text, isSuccess);
    }

    @Override
    public void showToastMessage(int textRes, Boolean isSuccess) {
        showSnackBar(getResources().getString(textRes), isSuccess);
    }

    private void showSnackBar(String text, Boolean isSuccess){
        Snackbar snack = generateSnackBar(text, isSuccess);
        snack.show();
    }

    public Snackbar generateSnackBar(String text, Boolean isSuccess){
        Snackbar snack = Snackbar.make(getRootView(), text, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        if(isSuccess){
            view.setBackground(getResources().getDrawable(R.drawable.blue_rounded_rectangle, null));
        } else {
            view.setBackground(getResources().getDrawable(R.drawable.red_rounded_rectangle, null));
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        }
        params.leftMargin = 30;
        params.rightMargin = 30;
        params.bottomMargin = 50;
        view.setLayoutParams(params);
        TextView textViewNoAct = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        textViewNoAct.setTextSize(15);
        textViewNoAct.setMaxLines(4);
        return snack;
    }

    public void animateActivityChangingToRight() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void animateActivityChangingToLeft() {
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void animateActivityChangingToTop() {
        overridePendingTransition(0, R.anim.top_out);
    }

    public void animateActivityChangingToBottom() {
        overridePendingTransition(0, R.anim.bottom_out);
    }

    @Override
    public void goBack() {
        close();
        animateActivityChangingToLeft();
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void onBackPressed() {
        // Overridden by goBack() method for handling default backPressed behaviour
        // super.onBackPressed();
        goBack();
    }


    @Override
    public void showProgress(boolean show) {
        showProgress(show, getResources().getString(R.string.dialog_progress_msg));
    }

    @Override
    public void showProgress(boolean show, String progressString) {
        if (show) {
            ProgressDialog dialog = DialogFactory.createProgressDialog(this, progressString);
            dialog.setCancelable(false);
            dialog.show();
            progressDlg = dialog;
        } else {
            if (null != progressDlg) {
                progressDlg.dismiss();
            }
        }
    }

    public boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public boolean isPortraitOnly() {
        return true;
    }

    public boolean isRestored() {
        return isRestored;
    }

    protected boolean doNotRestoreFragments() {
        return false;
    }

    protected abstract View getRootView();

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
