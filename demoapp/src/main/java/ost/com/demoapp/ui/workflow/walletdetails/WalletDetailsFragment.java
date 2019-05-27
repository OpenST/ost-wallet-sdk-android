/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.walletdetails;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstToken;

import org.json.JSONArray;
import org.json.JSONObject;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.entity.LogInUser;
import ost.com.demoapp.ui.BaseFragment;
import ost.com.demoapp.util.CommonUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletDetailsFragment extends BaseFragment implements View.OnClickListener {
    private OnWalletDetailsFragmentListener mListener;
    private LogInUser logInUser = AppProvider.get().getCurrentUser();
    private String viewEndPoint = AppProvider.get().getCurrentEconomy().getViewApiEndpoint();

    public WalletDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment WalletDetailsFragment.
     */
    public static WalletDetailsFragment newInstance() {
        WalletDetailsFragment fragment = new WalletDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWalletDetailsFragmentListener) {
            mListener = (OnWalletDetailsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWalletDetailsFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_wallet_details, container, false);
        AppBar appBar = AppBar.newInstance(getContext(), "Wallet Details", true);
        setUpAppBar(viewGroup, appBar);

        TextView mOstUserId = (TextView)viewGroup.findViewById(R.id.ost_user_id);
        mOstUserId.setText(logInUser.getOstUserId());
        mOstUserId.setOnClickListener(this);

        ((TextView)viewGroup.findViewById(R.id.ost_user_status)).setText(logInUser.getOstUser().getStatus());

        ((TextView)viewGroup.findViewById(R.id.ost_token_id)).setText(logInUser.getTokenId());

        TextView tokenHolderView = (TextView) viewGroup.findViewById(R.id.ost_user_token_holder);
        tokenHolderView.setText(logInUser.getOstUser().getTokenHolderAddress());
        tokenHolderView.setPaintFlags(tokenHolderView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tokenHolderView.setOnClickListener(this);

        TextView mOstUserDevice = (TextView)viewGroup.findViewById(R.id.ost_user_device_address);
        mOstUserDevice.setText(logInUser.getOstUser().getCurrentDevice().getAddress());
        mOstUserDevice.setOnClickListener(this);

        ((TextView)viewGroup.findViewById(R.id.ost_user_device_status)).setText(logInUser.getOstUser().getCurrentDevice().getStatus());

        TextView deviceManagerView = (TextView) viewGroup.findViewById(R.id.ost_user_device_manager_address);
        deviceManagerView.setText(logInUser.getOstUser().getDeviceManagerAddress());
        deviceManagerView.setPaintFlags(deviceManagerView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        deviceManagerView.setOnClickListener(this);

        TextView mOstUserRecoveryKey = (TextView)viewGroup.findViewById(R.id.ost_user_recovery_key_address);
        mOstUserRecoveryKey.setText(logInUser.getOstUser().getRecoveryAddress());
        mOstUserRecoveryKey.setOnClickListener(this);

        TextView recoveryOwnerView = (TextView) viewGroup.findViewById(R.id.ost_user_recovery_owner_address);
        recoveryOwnerView.setText(logInUser.getOstUser().getRecoveryOwnerAddress());
        recoveryOwnerView.setPaintFlags(recoveryOwnerView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        recoveryOwnerView.setOnClickListener(this);

        ((TextView)viewGroup.findViewById(R.id.ost_user_platform_endpoint)).setText(AppProvider.get().getCurrentEconomy().getSaasApiEndpoint());

        return viewGroup;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ost_user_id: {
                if(!logInUser.getOstUserId().equals("")){
                    copyTextToClipboard(logInUser.getOstUserId());
                }
                break;
            }
            case R.id.ost_user_device_address: {
                if(!logInUser.getOstUser().getCurrentDevice().getAddress().equals("")){
                    copyTextToClipboard(logInUser.getOstUser().getCurrentDevice().getAddress());
                }
                break;
            }
            case R.id.ost_user_recovery_key_address: {
                if(!logInUser.getOstUser().getRecoveryAddress().equals("")){
                    copyTextToClipboard(logInUser.getOstUser().getRecoveryAddress());
                }
                break;
            }
            case R.id.ost_user_token_holder: {
                try {
                    mListener.openWebView(new CommonUtils().getCurrentUserViewAddress());
                } catch (Exception e) {
                    Log.e("Exception", "Exception while getting chainId", e);
                }
                break;
            }
            case R.id.ost_user_device_manager_address: {
                OstToken token = OstSdk.getToken(logInUser.getTokenId());
                String url = viewEndPoint + "address/ad-" + token.getChainId() + "-" +
                        logInUser.getOstUser().getDeviceManagerAddress();
                mListener.openWebView(url);
                break;
            }
            case R.id.ost_user_recovery_owner_address: {
                OstToken token = OstSdk.getToken(logInUser.getTokenId());
                String url = viewEndPoint + "address/ad-" + token.getChainId() + "-" +
                        logInUser.getOstUser().getRecoveryOwnerAddress();
                mListener.openWebView(url);
                break;
            }
        }
    }

    public interface OnWalletDetailsFragmentListener {
        void openWebView(String url);
    }

    private void copyTextToClipboard(String text){
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }
}