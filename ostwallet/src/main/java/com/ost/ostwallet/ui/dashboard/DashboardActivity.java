/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.dashboard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.util.Objects;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.entity.Device;
import com.ost.ostwallet.entity.LogInUser;
import com.ost.ostwallet.entity.User;
import com.ost.ostwallet.network.MappyNetworkClient;
import com.ost.ostwallet.sdkInteract.SdkInteract;
import com.ost.ostwallet.sdkInteract.WorkFlowListener;
import com.ost.ostwallet.ui.BaseActivity;
import com.ost.ostwallet.ui.auth.OnBoardingActivity;
import com.ost.ostwallet.ui.managedevices.AuthorizeDeviceOptionsFragment;
import com.ost.ostwallet.ui.managedevices.DeviceListRecyclerViewAdapter;
import com.ost.ostwallet.ui.workflow.ChildFragmentStack;
import com.ost.ostwallet.ui.workflow.VerifyDeviceDataFragment;
import com.ost.ostwallet.ui.workflow.VerifyTransactionDataFragment;
import com.ost.ostwallet.ui.workflow.WorkFlowPinFragment;
import com.ost.ostwallet.ui.workflow.WorkFlowVerifyDataFragment;
import com.ost.ostwallet.ui.workflow.recovery.AbortRecoveryFragment;
import com.ost.ostwallet.ui.workflow.recovery.InitiateRecoveryFragment;
import com.ost.ostwallet.ui.workflow.transactions.TransactionFragment;
import com.ost.ostwallet.ui.workflow.walletdetails.WalletDetailsFragment;
import com.ost.ostwallet.ui.workflow.walletsetup.WalletSetUpFragment;
import com.ost.ostwallet.util.CommonUtils;
import com.ost.ostwallet.util.DialogFactory;
import com.ost.ostwallet.util.FragmentUtils;
import com.ost.ostwallet.util.KeyBoard;

import io.fabric.sdk.android.Fabric;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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
        TransactionFragment.OnFragmentInteractionListener,
        DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener,
        WalletDetailsFragment.OnWalletDetailsFragmentListener,
        AuthorizeDeviceOptionsFragment.OnAuthorizeDeviceOptionsFragmentListener,
        WalletFragment.walletFragmentInteraction {

    private static final String LOG_TAG = "DashboardActivity";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SettingsFragment mSettingsFragment;
    private WalletFragment mWalletFragment;
    private Boolean showUserActivationToast = false;
    private JSONObject transactionWorkflows = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);
        mSettingsFragment = SettingsFragment.newInstance();
        mWalletFragment = WalletFragment.newInstance();

        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        homePagerAdapter.addFragment(UserListFragment.newInstance(), "Users");
        homePagerAdapter.addFragment(mWalletFragment, "Wallet");
        homePagerAdapter.addFragment(mSettingsFragment, "Settings");

        mViewPager.setAdapter(homePagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.home_navigation);
        mTabLayout.setTabTextColors(Color.parseColor("#9b9b9b"), getResources().getColor(R.color.colorPrimary));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(R.drawable.users_icon);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setIcon(R.drawable.wallet_icon);
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setIcon(R.drawable.settings_icon);

        //Set SdkInteract Pin and verify data listeners
        SdkInteract.getInstance().setPinCallbackListener(this);
        SdkInteract.getInstance().setVerifyDataCallbackListener(this);
        SdkInteract.getInstance().setFlowListeners(this);

        setUpDevice();

        checkForActiveUserAndDevice();

    }

    private void setUpDevice() {
        OstWorkFlowCallback workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
        LogInUser logInUser = AppProvider.get().getCurrentUser();
        OstSdk.setupDevice(logInUser.getOstUserId(), logInUser.getTokenId(), workFlowListener);
    }

    private void checkForActiveUserAndDevice() {
        OstUser ostUser = AppProvider.get().getCurrentUser().getOstUser();
        if (!(ostUser.isActivated() || ostUser.isActivating())) {
            FragmentUtils.addFragment(R.id.layout_container,
                    WalletSetUpFragment.newInstance(),
                    this);
            mViewPager.setCurrentItem(1);
        } else if(ostUser.getCurrentDevice().canBeAuthorized()) {
            mSettingsFragment.setOpenDeviceAuthorization(true);
            mViewPager.setCurrentItem(2);
        } else {
            mViewPager.setCurrentItem(1);
            if (OstUser.CONST_STATUS.CREATED
                    .equalsIgnoreCase(
                            AppProvider.get().getCurrentUser().getStatus()
                    )) {
                notifyActivate();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        AppProvider.get().setCurrentActivity(this);
        new CommonUtils().showEconomyChangeDialog(intent, LOG_TAG, null);
    }

    @Override
    public void goBack() {
        Fragment topFragment = FragmentUtils.getTopFragment(this, R.id.layout_container);
        boolean consumed = false;
        if (topFragment instanceof ChildFragmentStack) {
             consumed = ((ChildFragmentStack)topFragment).popBack();
        }
        if (!consumed) {
            if (!FragmentUtils.isBackStackEmpty(this) &&
                    !(FragmentUtils.getTopFragment(this, R.id.layout_container) instanceof WalletSetUpFragment)) {
                FragmentUtils.goBack(this);
            } else {
                //hide keyboard if open
                KeyBoard.hideKeyboard(DashboardActivity.this);
                super.goBack();
            }
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
    }

    @Override
    public void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        showProgress(false);
        if (OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER
                .equals(
                        ostWorkflowContext.getWorkflow_type()
                )) {
            showUserActivationToast = true;
            notifyActivate();
            return;
        }
        JSONObject trxWorkflow = null;
        trxWorkflow = transactionWorkflows.optJSONObject(String.format("%s", workflowId));
        String successMessage = new CommonUtils().formatWorkflowSuccessToast(ostWorkflowContext.getWorkflow_type(), trxWorkflow);
        if(successMessage != null){
            if(trxWorkflow != null){
                refreshWalletAfter(500);
                showActionSnackBar(successMessage);
            } else {
                showToastMessage(successMessage, true);
            }
        }
    }

    private void notifyActivate() {
        refreshWalletAfter(30000);
        AppProvider.get().getMappyClient().notifyUserActivate(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.d(LOG_TAG, "Activate User Sync Succeeded");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(LOG_TAG, "Activate User Sync Failed");
            }
        });
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        showProgress(false);
        JSONObject trxWorkflow = null;
        try{
            trxWorkflow = transactionWorkflows.getJSONObject(String.format("%s", workflowId));
        } catch (Exception e){}
        String failMessage = new CommonUtils().formatWorkflowFailedToast(ostWorkflowContext.getWorkflow_type(), ostError, trxWorkflow);
        if(failMessage != null){
            showToastMessage(failMessage, false);
        }


        if (OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER
                .equals(
                        ostWorkflowContext.getWorkflow_type()
                )) {
            Log.e(LOG_TAG, "User Activate failed");
            checkForActiveUserAndDevice();
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
        showGetPinFragment(ostPinAcceptInterface);
    }

    @Override
    public void invalidPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        showProgress(false);
        showGetPinFragment(ostPinAcceptInterface);

        Dialog dialog = DialogFactory.createSimpleOkErrorDialog(DashboardActivity.this,
                "Incorrect PIN",
                "Please enter your valid PIN to authorize");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showGetPinFragment(OstPinAcceptInterface ostPinAcceptInterface) {
        WorkFlowPinFragment fragment = WorkFlowPinFragment.newInstance("Get Pin", getResources().getString(R.string.pin_sub_heading_get_pin));
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
        showProgress(false);
        JSONObject jsonObject;
        String dataToVerify = null;
        WorkFlowVerifyDataFragment fragment = null;
        if (OstSdk.DEVICE.equalsIgnoreCase(ostContextEntity.getEntityType())) {
            fragment = VerifyDeviceDataFragment.newInstance();
            OstDevice ostDevice = ((OstDevice) ostContextEntity.getEntity());
            fragment.setDataToVerify(ostDevice);
        } else {
            fragment = VerifyTransactionDataFragment.newInstance();
            jsonObject = (JSONObject) ostContextEntity.getEntity();
            fragment.setDataToVerify(jsonObject);
        }

        fragment.setVerifyDataCallback(ostVerifyDataInterface);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void popTopFragment() {
        FragmentUtils.goBack(this);
    }

    @Override
    public void onListFragmentInteraction(User user) {

        if (new CommonUtils().handleActionEligibilityCheck(DashboardActivity.this)) return;

        OstUser ostUser = AppProvider.get().getCurrentUser().getOstUser();
        if(!ostUser.getCurrentDevice().isAuthorized()){
            mViewPager.setCurrentItem(2);
            Fragment fragment = AuthorizeDeviceOptionsFragment.newInstance();
            FragmentUtils.addFragment(R.id.layout_container,
                    fragment,
                    this);
        } else {
            Fragment fragment = TransactionFragment.newInstance(user);
            FragmentUtils.addFragment(R.id.layout_container,
                    fragment,
                    this);
        }
    }

    @Override
    public void openWebView(String url) {
        WebViewFragment fragment = WebViewFragment.newInstance(url);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void onDeviceSelectToRevoke(Device device) {
        WorkFlowListener revokeDeviceWorkflowListener = SdkInteract.getInstance().newWorkFlowListener();

        OstSdk.revokeDevice(
                AppProvider.get().getCurrentUser().getOstUserId(),
                device.getDeviceAddress(),
                revokeDeviceWorkflowListener
        );
    }

    @Override
    public void onDeviceSelectedForRecovery(Device device) {
        Fragment fragment = InitiateRecoveryFragment.newInstance(device.getDeviceAddress());
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void onDeviceSelectedToAbortRecovery(Device device) {
        Fragment fragment = AbortRecoveryFragment.newInstance(device.getDeviceAddress());
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void setTransactionWorkflow(JSONObject transactionDetails){
        try{
            transactionWorkflows.put(transactionDetails.getString("workflowId"), transactionDetails);
        } catch (Exception e){}
    }

    @Override
    protected View getRootView() {
        return findViewById(R.id.layout_container);
    }

    @Override
    public void goToWalletDetails(){
        if(null != mViewPager){
            mViewPager.setCurrentItem(1);
        }
        refreshWalletAfter(10000);
    }

    private void showActionSnackBar(String text){
        Snackbar snack = generateSnackBar(text, true);
        TextView textViewNoAct = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_action);
        textViewNoAct.setTextSize(15);
        textViewNoAct.setTextColor(getResources().getColor(R.color.primary_button_text));
        snack.setAction("VIEW TX", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebView(new CommonUtils().getCurrentUserViewAddress());
            }
        });
        snack.show();
    }

    @Override
    public void relaunchApp() {
        AppProvider.get().getCookieStore().removeAll();
        Intent intent = new Intent(DashboardActivity.this, OnBoardingActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        DashboardActivity.this.startActivity(intent);
        DashboardActivity.this.finish();
    }

    private void refreshWalletAfter(int timeInMilliseconds){
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(null != mWalletFragment){
                            mWalletFragment.refreshWalletView();
                        }
                        if(showUserActivationToast){
                            showToastMessage(new CommonUtils().formatWorkflowSuccessToast(OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER, null), true);
                            showUserActivationToast = false;
                        }
                    }
                }, timeInMilliseconds);
    }
}