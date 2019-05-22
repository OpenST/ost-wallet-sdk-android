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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BaseFragment;
import ost.com.demoapp.ui.logging.WalletEventFragment;
import ost.com.demoapp.ui.managedevices.AuthorizeDeviceOptionsFragment;
import ost.com.demoapp.ui.managedevices.DeviceListFragment;
import ost.com.demoapp.ui.workflow.authrorizedeviceqr.AuthorizeDeviceQRFragment;
import ost.com.demoapp.ui.workflow.createsession.CreateSessionFragment;
import ost.com.demoapp.ui.workflow.entermnemonics.EnterMnemonicsFragment;
import ost.com.demoapp.ui.workflow.qrfragment.QRFragment;
import ost.com.demoapp.ui.workflow.recovery.AbortRecoveryFragment;
import ost.com.demoapp.ui.workflow.resetpin.ResetPinFragment;
import ost.com.demoapp.ui.workflow.viewmnemonics.ViewMnemonicsFragment;
import ost.com.demoapp.ui.workflow.walletdetails.WalletDetailsFragment;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.uicomponents.OstTextView;
import ost.com.demoapp.util.CommonUtils;

public class SettingsFragment extends BaseFragment implements
        SdkInteract.FlowInterrupt,
        SdkInteract.FlowComplete {
    private LinearLayout mScrollViewSettings;
    private OnFragmentInteractionListener mListener;
    public Boolean openDeviceAuthorization = false;
    private LayoutInflater mInflater;
    private ViewGroup mToggleBiometric;

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

        drawListItems();

        AppBar appBar = AppBar.newInstance(getContext(),
                "Wallet Settings",
                false);
        setUpAppBar(view, appBar);

        return view;
    }

    private void drawListItems(){
        mScrollViewSettings.removeAllViews();
        mScrollViewSettings.addView(getCategoryView("DEVICE"));

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

        View addSessionView = getFeatureView("Add Session", isUserActive);
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

        mToggleBiometric = (ViewGroup) getFeatureView(
                String.format("Biometric is %s",
                        OstSdk.isBiometricEnabled(AppProvider.get().getCurrentUser().getOstUserId()) ? "enabled":"disabled"
                ),
                isUserActive
        );
        mToggleBiometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActivatingStateCheck(getActivity())) return;

                String userId = AppProvider.get().getCurrentUser().getOstUserId();
                WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                showProgress(true, "Updating biometric...");
                SdkInteract.getInstance().subscribe(workFlowListener.getId(), SettingsFragment.this);
                OstSdk.updateBiometricPreference(userId, !OstSdk.isBiometricEnabled(userId), workFlowListener);
            }
        });
        mScrollViewSettings.addView(mToggleBiometric);

        mScrollViewSettings.addView(getCategoryView("ADD & Recovery"));

        View authorizeDeviceViaQR = getFeatureView("Authorize Device via QR", isUserActive);
        authorizeDeviceViaQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                Fragment fragment = AuthorizeDeviceQRFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(authorizeDeviceViaQR);

        View authorizeDeviceViaMnemonics = getFeatureView("Authorize Device via Mnemonics", isUserActive);
        authorizeDeviceViaMnemonics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                Fragment fragment = EnterMnemonicsFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(authorizeDeviceViaMnemonics);

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

        View initiateRecovery = getFeatureView("Initiate Recovery", isUserActive);
        initiateRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActionEligibilityCheck(getActivity())) return;

                Fragment fragment = DeviceListFragment.initiateRecoveryInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(initiateRecovery);

        View abortRecovery = getFeatureView("Abort Recovery", isUserActive);
        abortRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = AbortRecoveryFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(abortRecovery);

        View evenLogs = getFeatureView("Wallet events", true);
        evenLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = WalletEventFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(evenLogs);

        View viewLogOut = getFeatureView("Log out all sessions", isUserActive);
        viewLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new CommonUtils().handleActivatingStateCheck(getActivity())) return;

                if (userDeviceNotAuthorized()) {
                    openDeviceAuthorizationFragment();
                } else {
                    showProgress(true, "Logging Out");
                    WorkFlowListener workFlowListener = SdkInteract.getInstance().newWorkFlowListener();
                    OstSdk.logoutAllSessions(AppProvider.get().getCurrentUser().getOstUserId(), workFlowListener);
                    //Remove all cookies
                    AppProvider.get().getCookieStore().removeAll();
                }
            }
        });
        mScrollViewSettings.addView(viewLogOut);
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
        drawListItems();
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
        return mWalletSettingsItem;
    }

    private View getCategoryView(String categoryHeading) {
        OstTextView demoAppTextView = new OstTextView(getContext());
        demoAppTextView.setText(categoryHeading);
        demoAppTextView.setPadding(dpToPx(20), dpToPx(10), dpToPx(10), dpToPx(10));
        demoAppTextView.setTextSize(13);
        demoAppTextView.setTypeface(Typeface.DEFAULT_BOLD);
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

    private void updateCommonCode(OstWorkflowContext ostWorkflowContext) {
        if (OstWorkflowContext.WORKFLOW_TYPE.UPDATE_BIOMETRIC_PREFERENCE
                .equals(ostWorkflowContext.getWorkflow_type())) {
            OstTextView mTextView = mToggleBiometric.findViewById(R.id.ws_item);
            mTextView.setText(String.format("Biometric is %s",
                    OstSdk.isBiometricEnabled(AppProvider.get().getCurrentUser().getOstUserId()) ? "enabled":"disabled"
            ));
        }
    }

    interface OnFragmentInteractionListener {
        void launchFeatureFragment(Fragment fragment);
    }
}