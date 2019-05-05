/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONObject;

import java.util.Objects;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.entity.User;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.ui.BaseActivity;
import ost.com.demoapp.ui.workflow.WorkFlowPinFragment;
import ost.com.demoapp.ui.workflow.transactions.TransactionFragment;
import ost.com.demoapp.ui.workflow.walletsetup.WalletSetUpFragment;
import ost.com.demoapp.util.FragmentUtils;

public class DashboardActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener,
        SdkInteract.FlowComplete,
        SdkInteract.FlowInterrupt,
        SdkInteract.PinCallback,
        SdkInteract.VerifyDataCallback,
        UserListFragment.OnListFragmentInteractionListener,
        WalletSetUpFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        WorkFlowPinFragment.OnFragmentInteractionListener,
        TransactionFragment.OnFragmentInteractionListener {

    private static final String LOG_TAG = "DashboardActivity";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);

        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        homePagerAdapter.addFragment(UserListFragment.newInstance(), "Users");
        homePagerAdapter.addFragment(WalletFragment.newInstance(), "Wallet");
        homePagerAdapter.addFragment(SettingsFragment.newInstance(), "Wallet Settings");

        mViewPager.setAdapter(homePagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.home_navigation);
        mTabLayout.setTabTextColors(Color.parseColor("#9b9b9b"), getResources().getColor(R.color.colorPrimary));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(R.drawable.users_icon);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setIcon(R.drawable.wallet_icon);
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setIcon(R.drawable.settings_icon);

        mViewPager.setCurrentItem(1);

        //Set SdkInteract Pin and verify data listeners
        SdkInteract.getInstance().setPinCallbackListener(this);
        SdkInteract.getInstance().setVerifyDataCallbackListener(this);

        checkForActivateUser();
    }

    private void checkForActivateUser() {
        OstUser ostUser = AppProvider.get().getCurrentUser().getOstUser();
        if (!(ostUser.isActivated() || ostUser.isActivating())) {
            FragmentUtils.addFragmentWithoutBackStack(R.id.layout_container,
                    WalletSetUpFragment.newInstance(),
                    this);
        }
    }

    @Override
    public void goBack() {
        if (!FragmentUtils.isBackStackEmpty(this)) {
            FragmentUtils.goBack(this);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tab.getIcon().setTint(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        tab.getIcon().setTintList(null);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void activateAcknowledged(long workflowId) {
        FragmentUtils.goBack(this);
        SdkInteract.getInstance().subscribe(
                workflowId,
                this
        );

        SdkInteract.getInstance().subscribe(
                workflowId,
                this
        );
    }

    @Override
    public void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        if (OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER
                .equals(
                        ostWorkflowContext.getWorkflow_type()
                )){
            AppProvider.get().getMappyClient().notifyUserActivate(new MappyNetworkClient.ResponseCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Log.d(LOG_TAG,"Activate User Sync Succeded");
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.d(LOG_TAG,"Activate User Sync Failed");
                }
            });
        }
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        if (OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER
                .equals(
                        ostWorkflowContext.getWorkflow_type()
                )){
            Log.e(LOG_TAG, "User Activate failed");
            checkForActivateUser();
        }
    }

    @Override
    public void launchFeatureFragment(Fragment fragment) {
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void getPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        showProgress(false);
        WorkFlowPinFragment fragment = WorkFlowPinFragment.newInstance("Get Pin");
        fragment.setPinCallback(ostPinAcceptInterface);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void invalidPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        showProgress(false);
        WorkFlowPinFragment fragment = WorkFlowPinFragment.newInstance("Invalid Pin Try Again");
        fragment.setPinCallback(ostPinAcceptInterface);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void pinValidated(long workflowId, OstWorkflowContext ostWorkflowContext, String userId) {

    }

    @Override
    public void verifyData(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        ostVerifyDataInterface.dataVerified();
    }

    @Override
    public void popTopFragment() {
        FragmentUtils.goBack(this);
    }

    @Override
    public void onListFragmentInteraction(User user) {
        Fragment fragment = TransactionFragment.newInstance(user);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }
}