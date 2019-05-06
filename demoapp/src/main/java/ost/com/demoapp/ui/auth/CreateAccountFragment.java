/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.auth;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.entity.CurrentEconomy;
import ost.com.demoapp.ui.BaseFragment;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.uicomponents.PrimaryEditTextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateAccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAccountFragment extends BaseFragment {

    private static final String KEY_ID = "key_id";
    private OnFragmentInteractionListener mListener;
    private boolean mIsCreateAccountFragment = false;
    private PrimaryEditTextView editTextViewEconomy;

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateAccountFragment.
     */
    public static CreateAccountFragment newInstance(boolean isCreateAccountFragment) {
        CreateAccountFragment fragment = new CreateAccountFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_ID, isCreateAccountFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            mIsCreateAccountFragment = bundle.getBoolean(KEY_ID);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_create_account, container, false);
        editTextViewEconomy = view.findViewById(R.id.edv_economy);
        editTextViewEconomy.setRightDrawable(getResources().getDrawable(R.drawable.qr_icon, null));
        editTextViewEconomy.setHintText(getResources().getString(R.string.create_account_economy_hint));
        updateToken();
        editTextViewEconomy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.scanForEconomy();
            }
        });
        final PrimaryEditTextView editTextViewUserName = view.findViewById(R.id.edv_username);
        editTextViewUserName.setHintText(getResources().getString(R.string.create_account_username_hint));

        final PrimaryEditTextView editTextViewPassword = view.findViewById(R.id.edv_password);
        editTextViewPassword.setHintText(getResources().getString(R.string.create_account_password_hint));
        editTextViewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final Button button = ((Button) view.findViewById(R.id.pb_create_account));

        button.setText(mIsCreateAccountFragment ? "Create Account" : "Log In");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsCreateAccountFragment) {
                    mListener.createAccount(editTextViewEconomy.getText(), editTextViewUserName.getText(), editTextViewPassword.getText());
                } else {
                    mListener.logIn(editTextViewEconomy.getText(), editTextViewUserName.getText(), editTextViewPassword.getText());
                }
            }
        });

        AppBar appBar = AppBar.newInstance(getContext(), mIsCreateAccountFragment ? "Create Account" : "Log In", true);
        setUpAppBar(view, appBar);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public void updateToken() {
        CurrentEconomy currentEconomy = AppProvider.get().getCurrentEconomy();
        if (null != currentEconomy) {
            editTextViewEconomy.setText(currentEconomy.getTokenName());
        }
    }

    public interface OnFragmentInteractionListener {

        void createAccount(String economy, String userName, String password);

        void logIn(String economy, String userName, String password);

        void scanForEconomy();
    }
}
