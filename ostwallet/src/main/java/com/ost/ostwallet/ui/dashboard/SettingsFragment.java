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
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.crashlytics.android.Crashlytics;
import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.sdkInteract.SdkInteract;
import com.ost.ostwallet.sdkInteract.WorkFlowListener;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.ui.managedevices.AuthorizeDeviceOptionsFragment;
import com.ost.ostwallet.ui.managedevices.DeviceListFragment;
import com.ost.ostwallet.ui.workflow.authrorizedeviceqr.AuthorizeDeviceQRFragment;
import com.ost.ostwallet.ui.workflow.createsession.CreateSessionFragment;
import com.ost.ostwallet.ui.workflow.entermnemonics.EnterMnemonicsFragment;
import com.ost.ostwallet.ui.workflow.qrfragment.QRFragment;
import com.ost.ostwallet.ui.workflow.recovery.AbortRecoveryFragment;
import com.ost.ostwallet.ui.workflow.resetpin.ResetPinFragment;
import com.ost.ostwallet.ui.workflow.viewmnemonics.ViewMnemonicsFragment;
import com.ost.ostwallet.ui.workflow.walletdetails.WalletDetailsFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.uicomponents.OstTextView;
import com.ost.ostwallet.util.CommonUtils;
import com.ost.ostwallet.util.DialogFactory;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstJsonApi;
import com.ost.walletsdk.network.OstJsonApiCallback;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import io.reactivex.annotations.Nullable;

public class SettingsFragment extends BaseFragment implements
        SdkInteract.FlowInterrupt,
        SdkInteract.FlowComplete {
    private LinearLayout mScrollViewSettings;
    private OnFragmentInteractionListener mListener;
    public Boolean openDeviceAuthorization = false;
    private LayoutInflater mInflater;
    private ViewGroup mToggleBiometric;
    private View mAbortRecoveryView = null;
    private Boolean hasPendingRecoveries = false;
    private boolean onScreen = false;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOpenDeviceAuthorization(Boolean flagToOpen){
        openDeviceAuthorization = flagToOpen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);

        String mUserName = AppProvider.get().getCurrentUser().getUserName();

        TextDrawable.IBuilder mBuilder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .textColor(getResources().getColor(R.color.color_9b9b9b))
                .endConfig()
                .round();
        TextDrawable drawable = mBuilder.build(mUserName.substring(0,1).toUpperCase(), getResources().getColor(R.color.color_f4f4f4));

        ((ImageView) view.findViewById(R.id.ptv_image)).setImageDrawable(drawable);

        TextView textView = view.findViewById(R.id.ptv_username);
        textView.setText((mUserName.substring(0, 1).toUpperCase() + mUserName.substring(1)));

        TextView mUserIdTv = view.findViewById(R.id.ptv_userid);
        mUserIdTv.setText(AppProvider.get().getCurrentUser().getOstUserId());

        mScrollViewSettings = view.findViewById(R.id.ll_settings_list);

        mInflater = inflater;


        AppBar appBar = AppBar.newInstance(getContext(),
                "Settings",
                false);
        setUpAppBar(view, appBar);

        return view;
    }

    private void drawListItems(){

        mScrollViewSettings.removeAllViews();
        mScrollViewSettings.addView(getCategoryView("GENERAL"));

        OstUser ostUser = AppProvider.get().getCurrentUser().getOstUser();
        Boolean isUserActive = ostUser.isActivated();

        View walletDetailsView = getFeatureView("View Wallet Details", true);
        walletDetailsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new CommonUtils().handleActivatingStateCheck(getActivity())) return;

                Fragment fragment = WalletDetailsFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(walletDetailsView);

        View addSessionView = getFeatureView("Authenticate Wallet", isUserActive);
        addSessionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                if(userDeviceNotAuthorized()){
                    openDeviceAuthorizationFragment();
                } else {
                    Fragment fragment = CreateSessionFragment.newInstance();
                    mListener.launchFeatureFragment(fragment);
                }
            }
        });
        mScrollViewSettings.addView(addSessionView);

        View resetPinView = getFeatureView("Reset PIN", isUserActive);
        resetPinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                Fragment fragment = ResetPinFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(resetPinView);

        View viewMnemonicsView = getFeatureView("View Mnemonics", isUserActive);
        viewMnemonicsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                if (userDeviceNotAuthorized()) {
                    openDeviceAuthorizationFragment();
                } else {
                    Fragment fragment = ViewMnemonicsFragment.newInstance();
                    mListener.launchFeatureFragment(fragment);
                }
            }
        });
        mScrollViewSettings.addView(viewMnemonicsView);

        final ViewGroup fabricReporting = (ViewGroup) getFeatureView("Opt in to crash reporting", true);
        updatePostCrashAnalyticsView(fabricReporting);
        fabricReporting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AppProvider.FabricStateProvider fabricStateProvider = AppProvider.get().getFabricStateProvider();
                showProgress(true, String.format("Opting %s crash reporting", fabricStateProvider.isFabricOn() ? "out from": "in to"));
                fabricStateProvider.setUserDeviceFabricSetting(!fabricStateProvider.isFabricOn(), new AppProvider.FabricStateProvider.Callback() {
                    @Override
                    public void returnedPreference(Integer preference) {
                        showProgress(false);
                        if (null != preference) {
                            if (1 == preference && !fabricStateProvider.isFabricOn()) {
                                Fabric.with(getActivity(), new Crashlytics());
                                fabricStateProvider.setFabricOn(true);
                            }
                            if (0 == preference && fabricStateProvider.isFabricOn()) {
                                String title = "Opt out from crash reporting";
                                DialogFactory.createSimpleOkErrorDialog(AppProvider.get().getCurrentActivity(), title,
                                        "For the changes to take effect, please exit the app and re-launch it").show();
                                fabricStateProvider.setFabricOn(false);
                            }
                            updatePostCrashAnalyticsView(fabricReporting);
                        }
                    }
                });
            }
        });
        mScrollViewSettings.addView(fabricReporting);

        View contactSupportView = getFeatureView("Contact Support", true);
        contactSupportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = WebViewFragment.newInstance("https://help.ost.com/support/home", "OST Support");
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(contactSupportView);


        mScrollViewSettings.addView(getCategoryView("DEVICE"));

        View authorizeDeviceViaQR = getFeatureView("Authorize Additional Device via QR", isUserActive);
        authorizeDeviceViaQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                Fragment fragment = AuthorizeDeviceQRFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(authorizeDeviceViaQR);

        View authorizeDeviceViaMnemonics = getFeatureView("Authorize This Device via Mnemonics", isUserActive);
        authorizeDeviceViaMnemonics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                Fragment fragment = EnterMnemonicsFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(authorizeDeviceViaMnemonics);

        mToggleBiometric = (ViewGroup) getFeatureView("Enable Biometric Authentication", isUserActive);
        updateBiometricView(mToggleBiometric);
        mToggleBiometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActivatingStateCheck(getActivity())) return;

                if (new CommonUtils().isBioMetricHardwareAvailable() && !new CommonUtils().isBioMetricEnrolled()) {
                    new CommonUtils().showEnableBiometricDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    return;
                }
                String userId = AppProvider.get().getCurrentUser().getOstUserId();
                WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                showProgress(true, "Updating biometric...");
                SdkInteract.getInstance().subscribe(workFlowListener.getId(), SettingsFragment.this);
                OstSdk.updateBiometricPreference(userId, !OstSdk.isBiometricEnabled(userId), workFlowListener);
            }
        });
        if (new CommonUtils().isBioMetricHardwareAvailable()) {
            mScrollViewSettings.addView(mToggleBiometric);
        }

        View viewShowDeviceQR = getFeatureView("Show Device QR", true);
        viewShowDeviceQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActivatingStateCheck(getActivity())) return;

                Fragment fragment = QRFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(viewShowDeviceQR);

        View manageDevices = getFeatureView("Manage Devices", true);
        manageDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActivatingStateCheck(getActivity())) return;

                Fragment fragment = DeviceListFragment.manageDeviceInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(manageDevices);

        View transactionViaQR = getFeatureView("Transaction via QR", isUserActive);
        transactionViaQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                if (userDeviceNotAuthorized()) {
                    openDeviceAuthorizationFragment();
                } else {
                    //QR scanning is independent of workflow so AuthorizeDeviceQRFragment is used as generic for qr workflow
                    Fragment fragment = AuthorizeDeviceQRFragment.newInstance();
                    mListener.launchFeatureFragment(fragment);
                }
            }
        });
        mScrollViewSettings.addView(transactionViaQR);

        View initiateRecovery = getFeatureView("Initiate Recovery", ostUser.getCurrentDevice().canBeAuthorized());
        initiateRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = null;
                if(ostUser.getCurrentDevice().isRecovering()){
                    msg = "This device has recovery in progress. Other request cannot be initiated.";
                } else if (userDeviceNotAuthorized()) {
                    if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                    Fragment fragment = DeviceListFragment.initiateRecoveryInstance();
                    mListener.launchFeatureFragment(fragment);
                } else {
                    msg = "This is an authorized device, recovery applies only to cases where a user has no authorized device.";
                }
                if(null != msg){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AppProvider.get().getCurrentActivity());
                    builder.setMessage(msg);

                    builder.setPositiveButton("OK", null);
                    builder.create().show();
                }
            }
        });
        mScrollViewSettings.addView(initiateRecovery);

        mAbortRecoveryView = getFeatureView("Abort Recovery", hasPendingRecoveries);
        mAbortRecoveryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasPendingRecoveries){
                    Fragment fragment = AbortRecoveryFragment.newInstance();
                    mListener.launchFeatureFragment(fragment);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AppProvider.get().getCurrentActivity());
                    builder.setMessage("Recovery not initiated, Abort recovery applies only if recovery has been previously initiated.");

                    builder.setPositiveButton("OK", null);
                    builder.create().show();
                }
            }
        });
        mScrollViewSettings.addView(mAbortRecoveryView);

//        View evenLogs = getFeatureView("Wallet events", true);
//        evenLogs.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = WalletEventFragment.newInstance();
//                mListener.launchFeatureFragment(fragment);
//            }
//        });
//        mScrollViewSettings.addView(evenLogs);

        View sessionLogOut = getFeatureView("Revoke all Sessions", isUserActive);
        sessionLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActivatingStateCheck(getActivity())) return;

                if (userDeviceNotAuthorized()) {
                    openDeviceAuthorizationFragment();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AppProvider.get().getCurrentActivity());
                    builder.setMessage("Are you sure you want to revoke all sessions? You will need re-authenticate to spend tokens.");

                    builder.setPositiveButton("Revoke", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            showProgress(true, "Revoking Sessions...");
                            WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                            OstSdk.logoutAllSessions(AppProvider.get().getCurrentUser().getOstUserId(), workFlowListener);
                        }});
                    builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                }
            }
        });
        mScrollViewSettings.addView(sessionLogOut);

        View userLogout = getFeatureView("Logout", true);
        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AppProvider.get().getCurrentActivity());
                builder.setTitle("Sure you want to logout?");
                builder.setMessage("Are you sure you want to logout from OST Wallet");

                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Remove all cookies
                        AppProvider.get().getFabricStateProvider().clearUserFabricState();
                        AppProvider.get().getCookieStore().removeAll();
                        mListener.relaunchApp();
                    }});
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });
        mScrollViewSettings.addView(userLogout);

        mScrollViewSettings.addView(appBottomText());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsFragment.OnFragmentInteractionListener) {
            mListener = (SettingsFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        if(openDeviceAuthorization){
            openDeviceAuthorizationFragment();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        onScreen = true;
        drawListItems();
        fetchPendingRecoveries();
    }

    @Override
    public void onPause() {
        super.onPause();
        onScreen = false;
    }

    public void reDrawView() {
        if (onScreen) drawListItems();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private View getFeatureView(String featureTitle, Boolean isEnabled) {
        ViewGroup mWalletSettingsItem = (ViewGroup) mInflater.inflate(R.layout.wallet_settings_item, null, false);
        OstTextView mTextView = mWalletSettingsItem.findViewById(R.id.ws_item);
        mTextView.setText(featureTitle);
        if(!isEnabled){
            mTextView.setDisabled();
        }
        if(featureTitle.equalsIgnoreCase("logout")){
            mWalletSettingsItem.findViewById(R.id.ws_item_line).setVisibility(View.GONE);
        }
        return mWalletSettingsItem;
    }

    private View getCategoryView(String categoryHeading) {
        OstTextView demoAppTextView = new OstTextView(getContext());
        demoAppTextView.setText(categoryHeading);
        demoAppTextView.setPadding(dpToPx(20), dpToPx(10), dpToPx(10), dpToPx(10));
        demoAppTextView.setTextSize(13);
        demoAppTextView.setTypeface(Typeface.DEFAULT_BOLD);
        demoAppTextView.setTextColor(getResources().getColor(R.color.color_168dc1));
        return demoAppTextView;
    }

    private View appBottomText() {
        OstTextView demoAppTextView = new OstTextView(getContext());
        demoAppTextView.setText("This app version of OST Wallet is a test running on testnet, and transactions do not involve real money.");
        demoAppTextView.setPadding(dpToPx(30), dpToPx(30), dpToPx(30), dpToPx(20));
        demoAppTextView.setTextSize(15);
        demoAppTextView.setTextColor(getResources().getColor(R.color.color_9b9b9b));
        demoAppTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        return demoAppTextView;
    }

    private Boolean userDeviceNotAuthorized(){
        OstUser ostUser = AppProvider.get().getCurrentUser().getOstUser();
        return ostUser.getCurrentDevice().canBeAuthorized();
    }

    private void openDeviceAuthorizationFragment(){
        Fragment fragment = AuthorizeDeviceOptionsFragment.newInstance();
        mListener.launchFeatureFragment(fragment);
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        updateCommonCode(ostWorkflowContext);
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        updateCommonCode(ostWorkflowContext);
    }

    private void setAbortRecoveryView(){
        if(null != mAbortRecoveryView){
            if(hasPendingRecoveries){
                ((TextView)mAbortRecoveryView.findViewById(R.id.ws_item)).setTextColor(getResources().getColor(R.color.color_34445b));
            } else {
                ((OstTextView)mAbortRecoveryView.findViewById(R.id.ws_item)).setDisabled();
            }
        }
    }

    private void fetchPendingRecoveries() {
        OstJsonApi.getPendingRecovery(AppProvider.get().getCurrentUser().getOstUserId(), new OstJsonApiCallback() {
            @Override
            public void onOstJsonApiSuccess(@Nullable JSONObject jsonObject) {
                if ( null != jsonObject ) {
                    hasPendingRecoveries = true;
                } else {
                    Log.d("SettingsFragment", "fetchPendingRecoveries data is null.");
                    hasPendingRecoveries = false;
                }
                setAbortRecoveryView();
            }

            @Override
            public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject data) {
                Log.e("SettingsFragment", "fetchPendingRecoveries InternalErrorCode:" + err.getInternalErrorCode());
                hasPendingRecoveries = false;
                setAbortRecoveryView();
            }
        });
    }

    private void updateCommonCode(OstWorkflowContext ostWorkflowContext) {
        if (OstWorkflowContext.WORKFLOW_TYPE.UPDATE_BIOMETRIC_PREFERENCE
                .equals(ostWorkflowContext.getWorkflow_type())) {
            updateBiometricView(mToggleBiometric);
        }
    }

    private void updateBiometricView(ViewGroup toggleBiometric) {
        OstTextView mTextView = toggleBiometric.findViewById(R.id.ws_item);
        mTextView.setText(String.format("%s Biometric Authentication",
                OstSdk.isBiometricEnabled(AppProvider.get().getCurrentUser().getOstUserId()) ? "Disable":"Enable"
        ));
    }

    private void updatePostCrashAnalyticsView(ViewGroup crashAnalyticsView) {
        OstTextView mTextView = crashAnalyticsView.findViewById(R.id.ws_item);
        mTextView.setText(String.format("Opt %s crash reporting",
                AppProvider.get().getFabricStateProvider().isFabricOn() ? "out from":"in to"));
    }

    interface OnFragmentInteractionListener {
        void launchFeatureFragment(Fragment fragment);
        void relaunchApp();
    }
}