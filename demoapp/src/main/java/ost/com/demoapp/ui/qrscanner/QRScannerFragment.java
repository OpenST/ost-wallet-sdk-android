/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.qrscanner;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import ost.com.demoapp.customView.AppBar;
import ost.com.demoapp.ui.BaseFragment;

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

    private ZXingScannerView mScannerView;


    private OnFragmentInteractionListener mListener;
    private String mTitle;

    public QRScannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment QRScannerFragment.
     */
    public static QRScannerFragment newInstance(String title) {
        QRScannerFragment fragment = new QRScannerFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateViewDelegate"); // Prints scan results
        mScannerView = new ZXingScannerView(getContext());   // Programmatically initialize the scanner view
        container.addView(mScannerView); // Set the scanner view as the content view

        AppBar appBar = AppBar.newInstance(getContext(), mTitle, true);
        setUpAppBar(container, appBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE);
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
        } else {
            throw new RuntimeException(parentFragment.toString()
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