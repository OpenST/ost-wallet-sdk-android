package com.ost.ostwallet.ui.workflow;

import android.graphics.Color;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import com.ost.walletsdk.annotations.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Locale;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.uicomponents.OstBoldTextView;
import com.ost.ostwallet.uicomponents.OstTextView;
import com.ost.ostwallet.util.CommonUtils;
import com.ost.walletsdk.network.OstJsonApi;
import com.ost.walletsdk.network.OstJsonApiCallback;
import com.ost.walletsdk.workflows.errors.OstError;

public class VerifyTransactionDataFragment extends WorkFlowVerifyDataFragment {

    private JSONObject mVerifyDataJson;
    private BigDecimal totalTransferAmount = new BigDecimal("0");
    private Boolean isDirectTransfers = true;
    private JSONObject mPricePointData = null;
    private String mCurrencySign = "$";

    public static VerifyTransactionDataFragment newInstance() {
        VerifyTransactionDataFragment fragment = new VerifyTransactionDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    String getVerifyDataHeading() {
        return "Transaction";
    }

    String getPositiveButtonText() {
        return "Confirm Transaction";
    }

    @Override
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateViewDelegate(inflater, container, savedInstanceState);
        updateBalance();
    }

    @Override
    View getVerifyDataView() {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.view_verify_transaction, null);

        mVerifyDataJson = (JSONObject) getVerifyData();

        String pricerRule = mVerifyDataJson.optString(OstConstants.RULE_NAME);
        ((TextView)viewGroup.findViewById(R.id.atv_transfer_type)).setText(pricerRule.toUpperCase());
        isDirectTransfers = (pricerRule.equalsIgnoreCase("direct transfer"));
        LinearLayout transferHolder = ((LinearLayout)viewGroup.findViewById(R.id.ll_transfer_holder));

        JSONArray tokenHolderAddressesList = mVerifyDataJson.optJSONArray(OstConstants.TOKEN_HOLDER_ADDRESSES);
        JSONArray tokenHolderAmountsList = mVerifyDataJson.optJSONArray(OstConstants.AMOUNTS);
        JSONObject optionData = mVerifyDataJson.optJSONObject(OstConstants.TRANSACTION_OPTIONS);
        String currencySymbol = OstConfigs.getInstance().getPRICE_POINT_CURRENCY_SYMBOL();
        if (null != optionData) {
            currencySymbol = optionData.optString(OstConstants.QR_CURRENCY_CODE, OstConfigs.getInstance().getPRICE_POINT_CURRENCY_SYMBOL());
            mCurrencySign = optionData.optString(OstConstants.QR_CURRENCY_SIGN, "$");
        }

        updateBalanceView(viewGroup);

        for (int i=0; i<tokenHolderAddressesList.length(); i++) {
            String tokenHolderAddress = tokenHolderAddressesList.optString(i);
            String tokenHolderAmount = tokenHolderAmountsList.optString(i);
            totalTransferAmount = totalTransferAmount.add(new BigDecimal(tokenHolderAmount));
            LinearLayout layoutTransfer = new LinearLayout(getContext());
            layoutTransfer.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 20;
            layoutTransfer.setLayoutParams(layoutParams);

            OstTextView tokenHolderAddressView = new OstTextView(getContext());
            LinearLayout.LayoutParams paramsTokenHolder = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f);
            tokenHolderAddressView.setLayoutParams(paramsTokenHolder);
            tokenHolderAddressView.setText(tokenHolderAddress);
            tokenHolderAddressView.setTextColor(Color.parseColor("#34445b"));
            tokenHolderAddressView.setTextSize(13);

            layoutTransfer.addView(tokenHolderAddressView);

            OstBoldTextView tokenHolderValueView = new OstBoldTextView(getContext());
            LinearLayout.LayoutParams paramsTokenHolderValue = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f);
            tokenHolderValueView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            tokenHolderValueView.setLayoutParams(paramsTokenHolderValue);
            if(isDirectTransfers){
                tokenHolderValueView.setText(
                        String.format("%s %s", CommonUtils.convertWeiToTokenCurrency(tokenHolderAmount),
                                AppProvider.get().getCurrentEconomy().getTokenSymbol())
                );
            } else {
                tokenHolderValueView.setText(
                        String.format("%s %s", mCurrencySign ,CommonUtils.convertUsdWeitoUsd(tokenHolderAmount),
                                currencySymbol)
                );
            }
            tokenHolderValueView.setTextColor(Color.parseColor("#34445b"));
            tokenHolderValueView.setTextSize(14);


            layoutTransfer.addView(tokenHolderValueView);

            transferHolder.addView(layoutTransfer);
        }

        if(hasSufficientBalance()){
            ((TextView)viewGroup.findViewById(R.id.tv_not_enough_balance)).setVisibility(View.GONE);
        } else {
            ((TextView)viewGroup.findViewById(R.id.tv_not_enough_balance)).setVisibility(View.VISIBLE);
        }
        return viewGroup;
    }

    @Override
    public Boolean enablePrimaryButton(){
        return hasSufficientBalance();
    }

    void updateBalance() {
        showProgress(true, "Fetching User Balance");
        OstJsonApi.getBalanceWithPricePoints(AppProvider.get().getCurrentUser().getOstUserId(), new OstJsonApiCallback() {
            @Override
            public void onOstJsonApiSuccess(@Nullable JSONObject jsonObject) {
                if ( null != jsonObject ) {
                    String balance = "0";
                    try{
                        JSONObject balanceData = jsonObject.getJSONObject(jsonObject.getString(OstConstants.RESULT_TYPE));
                        balance = balanceData.getString("available_balance");
                        mPricePointData = jsonObject.optJSONObject("price_point");
                    } catch(Exception e){ }
                    AppProvider.get().getCurrentUser().updateBalance(balance);
                    showProgress(false);
                    refreshDataView();
                } else {
                    Log.d("VerifyTransactionData", "getBalanceWithPricePoints data is null.");
                    showProgress(false);
                }
            }

            @Override
            public void onOstJsonApiError(@NonNull OstError err, @Nullable JSONObject data) {
                Log.e("VerifyTransactionData", "getBalanceWithPricePoints InternalErrorCode:" + err.getInternalErrorCode());
                showProgress(false);
            }
        });
    }

    private Boolean hasSufficientBalance(){
        if(isDirectTransfers){
            return (totalTransferAmount.compareTo(new BigDecimal(AppProvider.get().getCurrentUser().getBalance())) <= 0 );
        } else {
            String userUsdBalance = CommonUtils.convertBTWeiToFiat(AppProvider.get().getCurrentUser().getBalance(), mPricePointData);
            if(null != userUsdBalance){
                String usdTransferAmount = CommonUtils.convertUsdWeitoUsd(totalTransferAmount.toString());
                return (new BigDecimal(usdTransferAmount).compareTo(new BigDecimal(userUsdBalance)) <= 0 );
            }
            return false;
        }
    }

    private void updateBalanceView(View viewGroup){
        if(isDirectTransfers){
            ((TextView) viewGroup.findViewById(R.id.tv_balance)).setText(String.format(Locale.getDefault(), "Balance: %s %s",
                    CommonUtils.convertWeiToTokenCurrency(AppProvider.get().getCurrentUser().getBalance()).toString(),
                    AppProvider.get().getCurrentEconomy().getTokenSymbol()));
        } else {
            ((TextView) viewGroup.findViewById(R.id.tv_balance)).setText(String.format(Locale.getDefault(), "Balance: %s %s",
                    mCurrencySign, CommonUtils.convertBTWeiToFiat(AppProvider.get().getCurrentUser().getBalance(), mPricePointData)));
        }
    }

    @Override
    public View refreshDataView(){
        View view = super.refreshDataView();
        updateBalanceView(view);
        return view;
    }

    String getTitle() {
        return "Confirm Transaction";
    }
}