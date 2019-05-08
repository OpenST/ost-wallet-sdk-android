/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.transactions;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.AppBar;
import ost.com.demoapp.uicomponents.OstPrimaryEditTextView;
import ost.com.demoapp.entity.User;
import ost.com.demoapp.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends BaseFragment implements TransactionsView {


    TransactionsPresenter mTransactionPresenter = TransactionsPresenter.getInstance();
    private User mUser;
    private OnFragmentInteractionListener mListener;

    public TransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static TransactionFragment newInstance(User user) {
        TransactionFragment fragment = new TransactionFragment();
        fragment.setUserData(user);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setUserData(User user) {
        mUser = user;
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
        if (context instanceof TransactionFragment.OnFragmentInteractionListener) {
            mListener = (TransactionFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup =  (ViewGroup) inflater.inflate(R.layout.fragment_transaction, container, true);

        ((TextView)viewGroup.findViewById(R.id.tv_balance)).setText(String.format("Balance: %s", mUser.getBalance()));

        /*********User View***********/
        viewGroup.findViewById(R.id.btn_send_token).setVisibility(View.GONE);

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getColor(mUser.getOstUserId());
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .round().build(mUser.getUserName().substring(0,1).toUpperCase(), color);

        ((ImageView)viewGroup.findViewById(R.id.iv_user_image)).setImageDrawable(drawable);

        ((TextView)viewGroup.findViewById(R.id.tv_user_name)).setText(mUser.getUserName());
        ((TextView)viewGroup.findViewById(R.id.tv_status)).setText(mUser.getTokenHolderAddress());
        /*************End*************/

        final OstPrimaryEditTextView tokensEditTextView = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_tokens_number));
        tokensEditTextView.setHintText(getResources().getString(R.string.transaction_amount));
        tokensEditTextView.setInputType(InputType.TYPE_CLASS_NUMBER);

        final OstPrimaryEditTextView unitEditTextView = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_tokens_unit));
        unitEditTextView.setHintText(getResources().getString(R.string.transaction_unit));

        ((Button)viewGroup.findViewById(R.id.pbtn_send_tokens)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransactionPresenter.sendTokens(mUser.getTokenHolderAddress(),
                        tokensEditTextView.getText(),
                        unitEditTextView.getText()
                );
            }
        });

        ((Button)viewGroup.findViewById(R.id.linkbtn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.popTopFragment();
            }
        });

        AppBar appBar = AppBar.newInstance(getContext(), "Send Tokens", true);
        setUpAppBar(viewGroup, appBar);

        mTransactionPresenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTransactionPresenter.detachView();
    }

    public interface OnFragmentInteractionListener {
        void popTopFragment();
    }
}