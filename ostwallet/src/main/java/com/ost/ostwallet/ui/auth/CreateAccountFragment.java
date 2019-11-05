/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.auth;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.entity.CurrentEconomy;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.uicomponents.OstPrimaryEditTextView;
import com.ost.ostwallet.util.KeyBoard;

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
    private OstPrimaryEditTextView editTextViewEconomy;
    private OstPrimaryEditTextView mEditTextViewUserName;
    private OstPrimaryEditTextView mEditTextViewPassword;

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
        editTextViewEconomy.setInputType(InputType.TYPE_NULL);
        editTextViewEconomy.setEnabled(false);
        updateToken();
        editTextViewEconomy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.scanForEconomy();
            }
        });
        mEditTextViewUserName = view.findViewById(R.id.edv_username);
        mEditTextViewUserName.setHintText(getResources().getString(R.string.create_account_username_hint));

        mEditTextViewPassword = view.findViewById(R.id.edv_password);
        mEditTextViewPassword.setHintText(getResources().getString(R.string.create_account_password_hint));
        mEditTextViewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final Button button = ((Button) view.findViewById(R.id.pb_create_account));

        button.setText(mIsCreateAccountFragment ? "Create Account" : "Log In");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoard.hideKeyboard(getContext());
                if (mIsCreateAccountFragment) {
                    mListener.createAccount(editTextViewEconomy.getText(), mEditTextViewUserName.getText(), mEditTextViewPassword.getText());
                } else {
                    mListener.logIn(editTextViewEconomy.getText(), mEditTextViewUserName.getText(), mEditTextViewPassword.getText());
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

    public void showUserNameError(String errorString) {
        mEditTextViewUserName.showErrorString(errorString);
    }

    public void showPasswordError(String errorString) {
        mEditTextViewPassword.showErrorString(errorString);
    }

    public interface OnFragmentInteractionListener {

        void createAccount(String economy, String userName, String password);

        void logIn(String economy, String userName, String password);

        void scanForEconomy();
    }
}
