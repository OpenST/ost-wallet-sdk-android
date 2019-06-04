/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.workflow.transactions;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.json.JSONObject;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.entity.User;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.uicomponents.OstPrimaryEditTextView;
import com.ost.ostwallet.util.CommonUtils;
import com.ost.walletsdk.OstConfigs;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends BaseFragment implements TransactionsView {


    TransactionsPresenter mTransactionPresenter;
    private User mUser;
    private OnFragmentInteractionListener mListener;
    private OstPrimaryEditTextView mTokensEditTextView;
    private OstPrimaryEditTextView mFiatAmountEditTextView;
    private String focusOnEtv = "";

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

        mTransactionPresenter = TransactionsPresenter.getInstance();
        mTransactionPresenter.attachView(this);

        ((TextView)viewGroup.findViewById(R.id.tv_balance)).setText(String.format("Balance: %s %s",
                CommonUtils.convertWeiToTokenCurrency(AppProvider.get().getCurrentUser().getBalance()),
                AppProvider.get().getCurrentEconomy().getTokenSymbol()));

        /*********User View***********/
        viewGroup.findViewById(R.id.btn_send_token).setVisibility(View.GONE);

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .textColor(getResources().getColor(R.color.color_9b9b9b))
                .endConfig()
                .round().build(mUser.getUserName().substring(0,1).toUpperCase(), getResources().getColor(R.color.color_f4f4f4));

        ((ImageView)viewGroup.findViewById(R.id.iv_user_image)).setImageDrawable(drawable);

        ((TextView)viewGroup.findViewById(R.id.tv_user_name)).setText(mUser.getUserName());
        ((TextView)viewGroup.findViewById(R.id.tv_status)).setText(mUser.getTokenHolderAddress());
        /*************End*************/

        mTokensEditTextView = ((OstPrimaryEditTextView)viewGroup.findViewById(R.id.etv_tokens_number));
        mTokensEditTextView.setHintText(getResources().getString(R.string.transaction_amount));
        mTokensEditTextView.setInputType((InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL));
        mTokensEditTextView.setOnFocusListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                focusOnEtv = AppProvider.get().getCurrentEconomy().getTokenSymbol();
            }
        });
        mTokensEditTextView.setOnTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(focusOnEtv.equals(AppProvider.get().getCurrentEconomy().getTokenSymbol())){
                    String usdVal = CommonUtils.convertBtToUsd(s.toString(), mTransactionPresenter.mPricePoint);
                    mFiatAmountEditTextView.setText((null != usdVal) ? usdVal : "");
                }
            }
        });

        OstPrimaryEditTextView symbolETv = (OstPrimaryEditTextView) viewGroup.findViewById(R.id.etv_tokens_unit);
        symbolETv.setHintText("Unit");
        symbolETv.setText(AppProvider.get().getCurrentEconomy().getTokenSymbol());
        symbolETv.diasbleInput();

        mFiatAmountEditTextView = ((OstPrimaryEditTextView) viewGroup.findViewById(R.id.etv_fiat_amount));
        mFiatAmountEditTextView.setHintText(getResources().getString(R.string.transaction_amount));
        mFiatAmountEditTextView.setInputType((InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL));
        mFiatAmountEditTextView.setOnFocusListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                focusOnEtv = OstConfigs.getInstance().PRICE_POINT_CURRENCY_SYMBOL;
            }
        });
        mFiatAmountEditTextView.setOnTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(focusOnEtv.equals(OstConfigs.getInstance().PRICE_POINT_CURRENCY_SYMBOL)){
                    String btVal = CommonUtils.convertUsdToBt(s.toString(), mTransactionPresenter.mPricePoint);
                    mTokensEditTextView.setText((null != btVal) ? btVal : "");
                }
            }
        });

        OstPrimaryEditTextView fiatUnitETv = (OstPrimaryEditTextView) viewGroup.findViewById(R.id.etv_fiat_unit);
        fiatUnitETv.setHintText("Unit");
        fiatUnitETv.setText(OstConfigs.getInstance().PRICE_POINT_CURRENCY_SYMBOL);
        fiatUnitETv.diasbleInput();

        ((Button)viewGroup.findViewById(R.id.pbtn_send_tokens)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTokensEditTextView.showErrorString(null);
                    JSONObject transactionDetails = mTransactionPresenter.sendTokens(mUser.getTokenHolderAddress(),
                            mTokensEditTextView.getText(),
                            AppProvider.get().getCurrentEconomy().getTokenSymbol()
                    );
                    if (null != transactionDetails) {
                        transactionDetails.put("userName", mUser.getUserName());
                        mListener.setTransactionWorkflow(transactionDetails);
                    }
                } catch (Exception e){}
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null != mFiatAmountEditTextView){
            mFiatAmountEditTextView.setOnTextChangeListener(null);
        }
        if(null != mTokensEditTextView){
            mTokensEditTextView.setOnTextChangeListener(null);
        }
        mTransactionPresenter.detachView();
        mTransactionPresenter = null;
    }

    @Override
    public void invalidTokenValue(String text) {
        mTokensEditTextView.showErrorString(text);
    }

    @Override
    public void insufficientBalance() {
        mTokensEditTextView.showErrorString("Not enough token balance");
    }

    public interface OnFragmentInteractionListener {
        void popTopFragment();
        void setTransactionWorkflow(JSONObject transactionDetails);
    }
}