/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.qrscanner;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import com.ost.ostwallet.R;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.OstBoldTextView;
import com.ost.ostwallet.uicomponents.OstSubHeaderTextView;
import com.ost.ostwallet.uicomponents.uiutils.SizeUtil;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.TRUE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QRScannerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QRScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QRScannerFragment extends BaseFragment implements ZXingScannerView.ResultHandler {

    private static final String LOG_TAG = "QRScannerFragment";
    private static final int QR_SCANNER_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final String TITLE = "title";
    private static final String SUB_HEADING = "sub_heading";

    private ZXingScannerView mScannerView;


    private OnFragmentInteractionListener mListener;
    private String mTitle;
    private String mSubHeading;

    public QRScannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QRScannerFragment.
     */
    public static QRScannerFragment newInstance(String title, String subHeading) {
        QRScannerFragment fragment = new QRScannerFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(SUB_HEADING, subHeading);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setBackgroundColor(Color.WHITE);
        relativeLayout.setClickable(true);
        relativeLayout.setFocusable(true);

        mScannerView = new ZXingScannerView(getContext());   // Programmatically initialize the scanner view
        relativeLayout.addView(mScannerView); // Set the scanner view as the content view
        showAppBar(false);

        TextView heading = new OstBoldTextView(getContext());
        heading.setId(R.id.btv_heading);
        heading.setText(mTitle);
        heading.setTextSize(SizeUtil.getTextSize(getResources(), R.dimen.sp_20));
        heading.setTextColor(getResources().getColor(R.color.qr_header_text));
        heading.setBackgroundColor(Color.WHITE);
        heading.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.qr_scanner_header_height));
        heading.setLayoutParams(params);
        relativeLayout.addView(heading);

        TextView subHeaderTextView = new OstSubHeaderTextView(getContext());
        subHeaderTextView.setText(mSubHeading);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.btv_heading);
        subHeaderTextView.setLayoutParams(params);
        subHeaderTextView.setBackgroundColor(Color.WHITE);
        subHeaderTextView.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.dp_15));
        relativeLayout.addView(subHeaderTextView);


        TextView qrFooter = new OstSubHeaderTextView(getContext());
        qrFooter.setText("Scanning in progress...");
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.qr_scanner_footer_height));
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        qrFooter.setLayoutParams(params);
        qrFooter.setGravity(Gravity.CENTER);
        qrFooter.setTextSize(SizeUtil.getTextSize(getResources(), R.dimen.qr_scanner_footer_text));
        qrFooter.setTextColor(Color.WHITE);
        qrFooter.setBackgroundColor(getResources().getColor(R.color.color_app_bar));
        relativeLayout.addView(qrFooter);


        ImageView crossImageView = new ImageView(getContext());
        crossImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_24dp, null));
        int padding = (int) getResources().getDimension(R.dimen.dp_15);
        crossImageView.setPadding(padding, padding, padding, padding);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
        crossImageView.setLayoutParams(params);
        crossImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        relativeLayout.addView(crossImageView);
        return relativeLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE);
            mSubHeading = getArguments().getString(SUB_HEADING);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume"); // Prints scan results
        if (ContextCompat.checkSelfPermission(getBaseActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getBaseActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    QR_SCANNER_PERMISSIONS_REQUEST_CAMERA);
        } else {
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();// Start camera on resume
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause"); // Prints scan results
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(LOG_TAG, rawResult.getText()); // Prints scan results
        Log.v(LOG_TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        String text = rawResult.getText();
        if (!TextUtils.isEmpty(text)) {
            Intent data = new Intent();
            data.setData(Uri.parse(text));
            mListener.onResultString(data);
        }
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case QR_SCANNER_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                        mScannerView.startCamera();// Start camera on resume
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        android.support.v4.app.Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof QRScannerFragment.OnFragmentInteractionListener) {
            mListener = (QRScannerFragment.OnFragmentInteractionListener) parentFragment;
        } else if (context instanceof QRScannerFragment.OnFragmentInteractionListener) {
            mListener = (QRScannerFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(parentFragment.toString() + "OR" + context.toString()
                    + " must implement QRScannerFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onResultString(Intent resultString);
    }
}