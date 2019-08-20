/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.util;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class WorkflowActivityLifecycleListener {
    private static final WorkflowActivityLifecycleListener INSTANCE = new WorkflowActivityLifecycleListener();
    private static final String LOG_TAG = "WFLifecycleListener";

    //Variables to save state of fragment Transaction
    private boolean mForeground = true;
    private int mContId = 0;
    private FragmentActivity mFragmentActivity = null;
    private String mTag = null;
    private FRAG_TXN frag_txn = null;
    private Fragment mFragment = null;

    enum FRAG_TXN {
        ADD_FRAGMENT,
        ADD_FRAGMENT_WO_BS,
        GO_BACK
    }

    public static WorkflowActivityLifecycleListener getInstance() {
        return INSTANCE;
    }



    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated");
    }


    public void onActivityResumed(Activity activity) {
        Log.d(LOG_TAG, "onActivityResumed");
        mForeground = true;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                runPendingFragmentTxn();
            }
        });
    }

    public void onActivityPaused(Activity activity) {
        Log.d(LOG_TAG, "onActivityPaused");
        mForeground = false;
    }


    public void onActivityDestroyed(Activity activity) {
        Log.d(LOG_TAG, "onActivityDestroyed");
        clearLastTxn();
    }

    public boolean isAppInBackground() {
        return !mForeground;
    }

    public void setPendingAddFragmentWithoutBackStack(int contId, Fragment fragment, FragmentActivity fragmentAct) {
        clearLastTxn();
        frag_txn = FRAG_TXN.ADD_FRAGMENT_WO_BS;
        mContId = contId;
        mFragmentActivity = fragmentAct;
        mFragment = fragment;
    }


    public void setPendingAddFragment(int contId, Fragment fragment, FragmentActivity fragmentAct, String tag) {
        clearLastTxn();
        frag_txn = FRAG_TXN.ADD_FRAGMENT;
        mContId = contId;
        mFragment = fragment;
        mFragmentActivity = fragmentAct;
        mTag = tag;
    }

    public void goBack(FragmentActivity fragmentAct) {
        if (null != mFragmentActivity) {
            clearLastTxn();
        } else {
            frag_txn = FRAG_TXN.GO_BACK;
            mFragmentActivity = fragmentAct;
        }
    }

    private void runPendingFragmentTxn() {
        if (null == frag_txn) return;
        switch (frag_txn) {
            case GO_BACK:
                FragmentUtils.goBack(mFragmentActivity);
                break;
            case ADD_FRAGMENT:
                FragmentUtils.addFragment(mContId, mFragment ,mFragmentActivity, mTag);
                break;
            case ADD_FRAGMENT_WO_BS:
                FragmentUtils.addFragmentWithoutBackStack(mContId, mFragment, mFragmentActivity);
        }
        clearLastTxn();
    }

    private void clearLastTxn() {
        frag_txn = null;
        mContId = 0;
        mFragment = null;
        mFragmentActivity = null;
        mTag = null;
    }
}