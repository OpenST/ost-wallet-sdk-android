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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.customView.AppBar;
import ost.com.demoapp.customView.DemoAppTextView;
import ost.com.demoapp.ui.BaseFragment;
import ost.com.demoapp.ui.workflow.walletdetails.WalletDetailsFragment;

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

        TextView textView = view.findViewById(R.id.ptv_username);
        textView.setText(AppProvider.get().getCurrentUser().getUserName());

        mScrollViewSettings = view.findViewById(R.id.ll_settings_list);

        mScrollViewSettings.addView(getCategoryView("GENERAL"));

        View walletDetailsView = getFeatureView("View Wallet Details");
        walletDetailsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = WalletDetailsFragment.newInstance();
                mListener.launchFeatureFragment(fragment);
            }
        });
        mScrollViewSettings.addView(walletDetailsView);

        View addSessionView = getFeatureView("Add Session");
        addSessionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mScrollViewSettings.addView(addSessionView);

        View resetPinView = getFeatureView("Reset Pin");
        resetPinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mScrollViewSettings.addView(resetPinView);

        View viewMnemonicsView = getFeatureView("View Mnemonics");
        viewMnemonicsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mScrollViewSettings.addView(viewMnemonicsView);


        mScrollViewSettings.addView(getCategoryView("DEVICE"));
        mScrollViewSettings.addView(getFeatureView("Authorize Device via QR"));
        mScrollViewSettings.addView(getFeatureView("Authorize Device via Mnemonics"));
        mScrollViewSettings.addView(getFeatureView("Show Device QR"));
        mScrollViewSettings.addView(getFeatureView("Manage Devices"));
        mScrollViewSettings.addView(getFeatureView("Transaction via QR"));
        mScrollViewSettings.addView(getFeatureView("Initiate Recovery"));
        mScrollViewSettings.addView(getFeatureView("Abort Recovery"));

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

    private View getFeatureView(String featureTitle) {
        DemoAppTextView demoAppTextView = new DemoAppTextView(getContext());
        demoAppTextView.setText(featureTitle);
        demoAppTextView.setTextSize(15);
        demoAppTextView.setPadding(10,30,10,30);
        Drawable drawableRightArrow = getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp, null);
        demoAppTextView.setCompoundDrawablesWithIntrinsicBounds(null,null, drawableRightArrow,null);
        return demoAppTextView;
    }

    private View getCategoryView(String categoryHeading) {
        DemoAppTextView demoAppTextView = new DemoAppTextView(getContext());
        demoAppTextView.setText(categoryHeading);
        demoAppTextView.setPadding(10,20,10,20);
        demoAppTextView.setTextSize(13);
        return demoAppTextView;
    }

    interface OnFragmentInteractionListener {
        void launchFeatureFragment(Fragment fragment);
    }
}