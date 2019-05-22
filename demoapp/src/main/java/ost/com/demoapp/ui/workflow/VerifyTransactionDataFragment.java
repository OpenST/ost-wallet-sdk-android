package ost.com.demoapp.ui.workflow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ost.walletsdk.OstConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Locale;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.uicomponents.OstBoldTextView;
import ost.com.demoapp.uicomponents.OstPrimaryButton;
import ost.com.demoapp.uicomponents.OstTextView;
import ost.com.demoapp.util.CommonUtils;

public class VerifyTransactionDataFragment extends WorkFlowVerifyDataFragment {

    private JSONObject mVerifyDataJson;
    private BigDecimal totalTransferAmount = new BigDecimal("0");
    private Boolean isDirectTransfers = true;
    private JSONObject mPricePointData = null;

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

        if(isDirectTransfers){
            ((TextView)viewGroup.findViewById(R.id.tv_balance)).setText(String.format(Locale.getDefault(), "Balance: %s %s",
                    CommonUtils.convertWeiToTokenCurrency(AppProvider.get().getCurrentUser().getBalance()).toString(),
                    AppProvider.get().getCurrentEconomy().getTokenSymbol()));
        } else {
            ((TextView)viewGroup.findViewById(R.id.tv_balance)).setText(String.format(Locale.getDefault(), "Balance: $ %s",
                    CommonUtils.convertBTWeiToUsd(AppProvider.get().getCurrentUser().getBalance(), mPricePointData)));
        }

        LinearLayout transferHolder = ((LinearLayout)viewGroup.findViewById(R.id.ll_transfer_holder));

        JSONArray tokenHolderAddressesList = mVerifyDataJson.optJSONArray(OstConstants.TOKEN_HOLDER_ADDRESSES);
        JSONArray tokenHolderAmountsList = mVerifyDataJson.optJSONArray(OstConstants.AMOUNTS);
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
                        String.format("$ %s", CommonUtils.convertUsdWeitoUsd(tokenHolderAmount),
                                "USD")
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
        AppProvider.get().getMappyClient().getCurrentUserBalance(new MappyNetworkClient.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                String balance = "0";
                if (new CommonUtils().isValidResponse(jsonObject)) {
                    balance = new CommonUtils().parseStringResponseForKey(jsonObject, "available_balance");
                    try{
                        JSONObject jsonData = jsonObject.getJSONObject(OstConstants.RESPONSE_DATA);
                        mPricePointData = jsonData.optJSONObject("price_point");
                    } catch(Exception e){ }
                }
                AppProvider.get().getCurrentUser().updateBalance(balance);
                showProgress(false);
                refreshDataView();
            }

            @Override
            public void onFailure(Throwable throwable) {
                showProgress(false);
            }
        });
    }

    private Boolean hasSufficientBalance(){
        if(isDirectTransfers){
            return (totalTransferAmount.compareTo(new BigDecimal(AppProvider.get().getCurrentUser().getBalance())) <= 0 );
        } else {
            String userUsdBalance = CommonUtils.convertBTWeiToUsd(AppProvider.get().getCurrentUser().getBalance(), mPricePointData);
            if(null != userUsdBalance){
                return (totalTransferAmount.compareTo(new BigDecimal(userUsdBalance)) <= 0 );
            }
            return false;
        }
    }

    String getTitle() {
        return "Confirm Transaction";
    }
}
