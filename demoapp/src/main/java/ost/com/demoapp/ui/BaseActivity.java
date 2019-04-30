/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui;


import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ost.com.demoapp.R;
import ost.com.demoapp.util.DialogFactory;


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
    public void showToastMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToastMessage(int textRes) {
        Toast.makeText(this, getString(textRes), Toast.LENGTH_LONG).show();
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
        if (show) {
            ProgressDialog dialog = DialogFactory.createProgressDialog(this, R.string.dialog_progress_msg);
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
}
