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

import android.app.AlertDialog;
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
import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.entity.Device;
import com.ost.ostwallet.entity.LogInUser;
import com.ost.ostwallet.entity.User;
import com.ost.ostwallet.network.MappyNetworkClient;
import com.ost.ostwallet.ui.BaseActivity;
import com.ost.ostwallet.ui.auth.OnBoardingActivity;
import com.ost.ostwallet.ui.managedevices.AuthorizeDeviceOptionsFragment;
import com.ost.ostwallet.ui.managedevices.DeviceListRecyclerViewAdapter;
import com.ost.ostwallet.ui.workflow.ChildFragmentStack;
import com.ost.ostwallet.ui.workflow.WorkFlowPinFragment;
import com.ost.ostwallet.ui.workflow.createsession.CreateSessionFragment;
import com.ost.ostwallet.ui.workflow.transactions.TransactionFragment;
import com.ost.ostwallet.ui.workflow.walletdetails.WalletDetailsFragment;
import com.ost.ostwallet.ui.workflow.walletsetup.WalletSetUpFragment;
import com.ost.ostwallet.util.CommonUtils;
import com.ost.ostwallet.util.DialogFactory;
import com.ost.ostwallet.util.FragmentUtils;
import com.ost.ostwallet.util.KeyBoard;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.ui.OstWalletUI;
import com.ost.walletsdk.ui.interfaces.FlowCompleteListener;
import com.ost.walletsdk.ui.interfaces.FlowInterruptListener;
import com.ost.walletsdk.ui.interfaces.RequestAcknowledgedListener;
import com.ost.walletsdk.ui.sdkInteract.SdkInteract;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DashboardActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener,
        FlowCompleteListener,
        FlowInterruptListener,
        RequestAcknowledgedListener,
        UserListFragment.OnListFragmentInteractionListener,
        WalletSetUpFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        WorkFlowPinFragment.OnFragmentInteractionListener,
        TransactionFragment.OnFragmentInteractionListener,
        DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener,
        WalletDetailsFragment.OnWalletDetailsFragmentListener,
        AuthorizeDeviceOptionsFragment.OnAuthorizeDeviceOptionsFragmentListener,
        WalletFragment.walletFragmentInteraction,
        CreateSessionFragment.OnFragmentInteractionListener {

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
            long expiredAfterInSecs = 30 * 24 * 60 * 60;
            Integer decimals = Integer.parseInt(OstToken.getById(AppProvider.get().getCurrentUser().getTokenId()).getBtDecimals());
            String spendingLimit = new BigDecimal("1000").multiply(new BigDecimal(10).pow(decimals)).toString();
            String workflowId = OstWalletUI.activateUser(DashboardActivity.this,
                    ostUser.getId(),
                    expiredAfterInSecs,
                    spendingLimit,
                    AppProvider.get().getUserPassphraseCallback());
            OstWalletUI.subscribe(workflowId, this);
            mViewPager.setCurrentItem(1);
        } else if(null != ostUser.getCurrentDevice() && ostUser.getCurrentDevice().canBeAuthorized()) {
            handleCrashAnalytics();
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
            handleCrashAnalytics();
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
    public void activateAcknowledged(String workflowId) {
        FragmentUtils.goBack(this);
        handleCrashAnalytics();
    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        String workflowId = ostWorkflowContext.getWorkflowId();
        showProgress(false);
        if (OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER
                .equals(
                        ostWorkflowContext.getWorkflowType()
                )) {
            showUserActivationToast = true;
            notifyActivate();
            return;
        } else if (OstWorkflowContext.WORKFLOW_TYPE.UPDATE_BIOMETRIC_PREFERENCE
                .equals(
                        ostWorkflowContext.getWorkflowType()
                )) {
            mSettingsFragment.reDrawView();
        }
        JSONObject trxWorkflow = null;
        trxWorkflow = transactionWorkflows.optJSONObject(String.format("%s", workflowId));
        String successMessage = new CommonUtils().formatWorkflowSuccessToast(ostWorkflowContext.getWorkflowType(), trxWorkflow);
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
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        showProgress(false);

        Log.d(LOG_TAG, String.format("Flow Interrupted: WorkFlow Id: %s of Workflow %s", ostWorkflowContext.getWorkflowId() ,ostWorkflowContext.getWorkflowType().toString()));

        //region Sdk Error Handling
        if (OstErrors.ErrorCode.WORKFLOW_CANCELLED == ostError.getErrorCode()) {
            //Test-App has cancelled the workflow
            Log.i(LOG_TAG, "Interrupt Reason: Test-App cancelled the workflow.");
        } else if (ostError.isApiError()) {
            OstApiError apiError = (OstApiError) ostError;
            if (apiError.isApiSignerUnauthorized()) {
                // The device has been revoked and can not make any more calls to OST Platform.
                // Apps must logout users at this point.
                // For purpose of testing the sdk, lets give users an option.
                deviceUnauthorized(apiError);
            } else {
                //Let's log the error
                logSdkError(ostWorkflowContext, ostError);
            }
        } else if (OstErrors.ErrorCode.DEVICE_NOT_SETUP == ostError.getErrorCode()) {
            // Device needs to be registered or new device keys need to be created.
            // To perform this operation, Test-App needs to call OstSdk.setupDevice()
            // If app believe that user is authenticated, they should logout user here.
            deviceUnauthorized(ostError);
        } else {
            //Let's log the error
            logSdkError(ostWorkflowContext, ostError);
        }

        JSONObject trxWorkflow = null;
        try{
            trxWorkflow = transactionWorkflows.getJSONObject(String.format("%s", ostWorkflowContext.getWorkflowId()));
        } catch (Exception e){}
        String failMessage = new CommonUtils().formatWorkflowFailedToast(ostWorkflowContext.getWorkflowType(), ostError, trxWorkflow);
        if(failMessage != null){
            showToastMessage(failMessage, false);
        }


        if (OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER
                .equals(
                        ostWorkflowContext.getWorkflowType()
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


    private void showGetPinFragment(OstPinAcceptInterface ostPinAcceptInterface) {
        WorkFlowPinFragment fragment = WorkFlowPinFragment.newInstance("Get Pin", getResources().getString(R.string.pin_sub_heading_get_pin));
        fragment.setPinCallback(ostPinAcceptInterface);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void popTopFragment() {
        FragmentUtils.goBack(this);
    }

    @Override
    public void invalidPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

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
//        WorkFlowListener revokeDeviceWorkflowListener = SdkInteract.getInstance().newWorkFlowListener();
//
//        OstSdk.revokeDevice(
//                AppProvider.get().getCurrentUser().getOstUserId(),
//                device.getDeviceAddress(),
//                revokeDeviceWorkflowListener
//        );
    }

    @Override
    public void onDeviceSelectedForRecovery(Device device) {
        String deviceAddress = null;
        if (null != device) deviceAddress = device.getDeviceAddress();

        String workflowId = OstWalletUI.initiateDeviceRecovery(this,
                AppProvider.get().getCurrentUser().getOstUserId(),
                deviceAddress,
                AppProvider.get().getUserPassphraseCallback());
        OstWalletUI.subscribe(workflowId, this);
    }

    @Override
    public void initiateDeviceRecovery() {
        onDeviceSelectedForRecovery(null);
    }

    @Override
    public void resetPin() {
        String workflowId = OstWalletUI.resetPin(this,
                AppProvider.get().getCurrentUser().getOstUserId(),
                AppProvider.get().getUserPassphraseCallback());
        OstWalletUI.subscribe(workflowId, this);
    }

    @Override
    public void updateBiometricPreference(boolean enable) {
        String userId = AppProvider.get().getCurrentUser().getOstUserId();

        String workflowId = OstWalletUI.updateBiometricPreference(this,
                userId,
                enable,
                AppProvider.get().getUserPassphraseCallback());
        OstWalletUI.subscribe(workflowId, this);
    }

    @Override
    public void viewMnemonics() {
        String workflowId = OstWalletUI.getDeviceMnemonics(this,
                AppProvider.get().getCurrentUser().getOstUserId(),
                AppProvider.get().getUserPassphraseCallback());
        OstWalletUI.subscribe(workflowId, this);
    }

    @Override
    public void revokeDevice() {
        String workflowId = OstWalletUI.revokeDevice(this,
                AppProvider.get().getCurrentUser().getOstUserId(),
                null,
                AppProvider.get().getUserPassphraseCallback());
        OstWalletUI.subscribe(workflowId, this);
    }

    @Override
    public void authorizeDeviceWithMnemonics() {
        String workflowId = OstWalletUI.authorizeCurrentDeviceWithMnemonics(this,
                AppProvider.get().getCurrentUser().getOstUserId(),
                AppProvider.get().getUserPassphraseCallback());
        OstWalletUI.subscribe(workflowId, this);
    }

    @Override
    public void showAddDeviceQR() {
        String workflowId = OstWalletUI.getAddDeviceQRCode(this,
                AppProvider.get().getCurrentUser().getOstUserId());
        OstWalletUI.subscribe(workflowId, this);
    }

    @Override
    public void onDeviceSelectedToAbortRecovery(Device device) {
        String workflowId = OstWalletUI.abortDeviceRecovery(this,
                AppProvider.get().getCurrentUser().getOstUserId(),
                AppProvider.get().getUserPassphraseCallback());
        OstWalletUI.subscribe(workflowId, this);
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

    private void handleCrashAnalytics() {
        final AppProvider.FabricStateProvider fabricStateProvider = AppProvider.get().getFabricStateProvider();
        fabricStateProvider.getUserDeviceFabricSetting(new AppProvider.FabricStateProvider.Callback() {
            @Override
            public void returnedPreference(Integer preference) {
                if (preference == -1) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AppProvider.get().getCurrentActivity());
                    builder.setTitle("Crash Reporting");
                    builder.setMessage("Would you like to share crash reports with OST to help improve the app?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Opt in", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            fabricStateProvider.setUserDeviceFabricSetting(true, new AppProvider.FabricStateProvider.Callback() {
                                @Override
                                public void returnedPreference(Integer preference) {
                                    if (!fabricStateProvider.isFabricOn()) {
                                        Fabric.with(AppProvider.get().getApplicationContext(), new Crashlytics());
                                        fabricStateProvider.setFabricOn(true);
                                        mSettingsFragment.reDrawView();
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Opt out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            fabricStateProvider.setUserDeviceFabricSetting(false, new AppProvider.FabricStateProvider.Callback() {
                                @Override
                                public void returnedPreference(Integer preference) {
                                    if (fabricStateProvider.isFabricOn()) {
                                        fabricStateProvider.setFabricOn(false);
                                        mSettingsFragment.reDrawView();
                                        String title = "Opt out from crash reporting";
                                        DialogFactory.createSimpleOkErrorDialog(AppProvider.get().getCurrentActivity(), title,
                                                "For the changes to take effect, please exit the app and re-launch it").show();
                                    }
                                }
                            });
                        }
                    });
                    builder.create().show();
                } else if (preference == 0) {
                    if (fabricStateProvider.isFabricOn()) {
                        fabricStateProvider.setFabricOn(false);
                        mSettingsFragment.reDrawView();
                        String title = "Opt out from crash reporting";
                        DialogFactory.createSimpleOkErrorDialog(AppProvider.get().getCurrentActivity(), title,
                                "For the changes to take effect, please exit the app and re-launch it").show();
                    }
                } else if (preference == 1){
                    if (!fabricStateProvider.isFabricOn()) {
                        Fabric.with(AppProvider.get().getApplicationContext(), new Crashlytics());
                        fabricStateProvider.setFabricOn(true);
                        mSettingsFragment.reDrawView();
                    }
                }
            }
        });
    }

    @Override
    public void createSession(String spendingLimit, long expiryTime) {
        String workflowId = OstWalletUI.createSession(this,
                AppProvider.get().getCurrentUser().getOstUserId(),
                expiryTime,
                spendingLimit,
                AppProvider.get().getUserPassphraseCallback());
        OstWalletUI.subscribe(workflowId, this);
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        if (OstWorkflowContext.WORKFLOW_TYPE.ADD_SESSION.equals(ostWorkflowContext.getWorkflowType())) {
            showToastMessage("Session authorization request received.", true);
        } else if (OstWorkflowContext.WORKFLOW_TYPE.REVOKE_DEVICE.equals(ostWorkflowContext.getWorkflowType())) {
            showToastMessage("Device Revoke request received.", true);
        } else if (OstWorkflowContext.WORKFLOW_TYPE.AUTHORIZE_DEVICE_WITH_MNEMONICS.equals(ostWorkflowContext.getWorkflowType())) {
            showToastMessage("Authorize device request received", true);
        }
    }

    private void logSdkError(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        StringBuilder errorStringBuilder = new StringBuilder();

        String errorString = String.format("Error: %s " +
                        "\nwith error code: %s" +
                        "\ninternal error code: %s",
                ostError.getMessage(),
                ostError.getErrorCode(),
                ostError.getInternalErrorCode()
        );
        errorStringBuilder.append(errorString);

        if (ostError.isApiError()) {
            OstApiError ostApiError = ((OstApiError) ostError);
            String apiErrorCodeMsg = String.format(
                    "\n%s: %s",
                    ostApiError.getErrCode(),
                    ostApiError.getErrMsg());

            errorStringBuilder.append(apiErrorCodeMsg);

            errorStringBuilder.append(
                    String.format("\napi_internal_id: %s", ostApiError.getApiInternalId())
            );

            List<OstApiError.ApiErrorData> apiErrorDataList = ostApiError.getErrorData();
            for (OstApiError.ApiErrorData apiErrorData : apiErrorDataList) {
                String errorData = String.format(
                        "\n%s: %s",
                        apiErrorData.getParameter(),
                        apiErrorData.getMsg());

                errorStringBuilder.append(errorData);
            }
        }
        String stringFormattedError = errorStringBuilder.toString();
        Log.e(LOG_TAG, stringFormattedError);

    }

    private void deviceUnauthorized(OstError ostError) {
        String title = "Device not registered";
        String message = "Please login again to register your device.";
        if (ostError.isApiError()) {
            OstApiError apiError = (OstApiError) ostError;
            if (apiError.isApiSignerUnauthorized()) {
                title = "Device Revoked";
            }
        }
        Log.e(LOG_TAG, title);
        showLogoutDialog(title, message);
    }

    public void showLogoutDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AppProvider.get().getCurrentActivity());
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AppProvider.get().relaunchApp();
            }});
        builder.create().show();
    }
}