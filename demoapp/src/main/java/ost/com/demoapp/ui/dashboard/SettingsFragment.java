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
import android.content.Intent;
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

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.ui.logging.WalletEventFragment;
import ost.com.demoapp.ui.logging.WalletEventFragment;
import ost.com.demoapp.ui.workflow.recovery.AbortRecoveryFragment;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.uicomponents.OstTextView;
import ost.com.demoapp.ui.BaseFragment;
import ost.com.demoapp.ui.auth.OnBoardingActivity;
import ost.com.demoapp.ui.managedevices.DeviceListFragment;
import ost.com.demoapp.ui.workflow.authrorizedeviceqr.AuthorizeDeviceQRFragment;
import ost.com.demoapp.ui.workflow.createsession.CreateSessionFragment;
import ost.com.demoapp.ui.workflow.entermnemonics.EnterMnemonicsFragment;
import ost.com.demoapp.ui.workflow.qrfragment.QRFragment;
import ost.com.demoapp.ui.workflow.resetpin.ResetPinFragment;
import ost.com.demoapp.ui.workflow.viewmnemonics.ViewMnemonicsFragment;
import ost.com.demoapp.ui.workflow.walletdetails.WalletDetailsFragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SettingsFragment extends BaseFragment {
    private LinearLayout mScrollViewSettings;
    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        textView.setText((mUserName.substring(0,1).toUpperCase() + mUserName.substring(1)));

        TextView mUserIdTv = view.findViewById(R.id.ptv_userid);
        mUserIdTv.setText(AppProvider.get().getCurrentUser().getOstUserId());

        mScrollViewSettings = view.findViewById(R.id.ll_settings_list);

        mScrollViewSettings.addView(getCategoryView("DEVICE"));

        View walletDetailsView = getFeatureView("View Wallet Details", inflater);
        walletDetailsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = WalletDetailsFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(walletDetailsView);

        View addSessionView = getFeatureView("Add Session", inflater);
        addSessionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = CreateSessionFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(addSessionView);

        View resetPinView = getFeatureView("Reset PIN", inflater);
        resetPinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = ResetPinFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(resetPinView);

        View viewMnemonicsView = getFeatureView("View Mnemonics", inflater);
        viewMnemonicsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = ViewMnemonicsFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(viewMnemonicsView);


        mScrollViewSettings.addView(getCategoryView("ADD & Recovery"));

        View authorizeDeviceViaQR = getFeatureView("Authorize Device via QR", inflater);
        authorizeDeviceViaQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = AuthorizeDeviceQRFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(authorizeDeviceViaQR);

        View authorizeDeviceViaMnemonics = getFeatureView("Authorize Device via Mnemonics", inflater);
        authorizeDeviceViaMnemonics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = EnterMnemonicsFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(authorizeDeviceViaMnemonics);

        View viewShowDeviceQR = getFeatureView("Show Device QR", inflater);
        viewShowDeviceQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = QRFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(viewShowDeviceQR);

        View manageDevices = getFeatureView("Manage Devices", inflater);
        manageDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = DeviceListFragment.manageDeviceInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(manageDevices);

        View transactionViaQR = getFeatureView("Transaction via QR", inflater);
        transactionViaQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //QR scanning is independent of workflow so AuthorizeDeviceQRFragment is used as generic for qr workflow
                Fragment fragment = AuthorizeDeviceQRFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(transactionViaQR);

        View initiateRecovery = getFeatureView("Initiate Recovery", inflater);
        initiateRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = DeviceListFragment.initiateRecoveryInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(initiateRecovery);

        View abortRecovery = getFeatureView("Abort Recovery", inflater);
        abortRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = AbortRecoveryFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(abortRecovery);

        View evenLogs = getFeatureView("Wallet events", inflater);
        evenLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = WalletEventFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(evenLogs);

        View viewLogOut = getFeatureView("Log out", inflater);
        viewLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppProvider.get().getCookieStore().removeAll();
                Intent intent = new Intent(getContext(), OnBoardingActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        mScrollViewSettings.addView(viewLogOut);

        AppBar appBar = AppBar.newInstance(getContext(),
                "Wallet Settings",
                false);
        setUpAppBar(view, appBar);

        return view;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private View getFeatureView(String featureTitle, LayoutInflater inflater) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.wallet_settings_item, null, false);
        OstTextView mTextView = view.findViewById(R.id.ws_item);
        mTextView.setText(featureTitle);
        return view;
    }

    private View getCategoryView(String categoryHeading) {
        OstTextView demoAppTextView = new OstTextView(getContext());
        demoAppTextView.setText(categoryHeading);
        demoAppTextView.setPadding(dpToPx(20),dpToPx(10),dpToPx(10),dpToPx(10));
        demoAppTextView.setTextSize(13);
        demoAppTextView.setTypeface(Typeface.DEFAULT_BOLD);
        return demoAppTextView;
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    interface OnFragmentInteractionListener {
        void launchFeatureFragment(Fragment fragment);
    }
}