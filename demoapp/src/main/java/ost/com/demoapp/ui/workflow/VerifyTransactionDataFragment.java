package ost.com.demoapp.ui.workflow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ost.walletsdk.OstConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.OstBoldTextView;
import ost.com.demoapp.uicomponents.OstTextView;
import ost.com.demoapp.util.CommonUtils;

public class VerifyTransactionDataFragment extends WorkFlowVerifyDataFragment {

    private JSONObject mVerifyDataJson;

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
        return "Authorize Transaction";
    }

    @Override
    View getVerifyDataView() {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.view_verify_transaction, null);

        ((TextView)viewGroup.findViewById(R.id.tv_balance)).setText(String.format(Locale.getDefault(), "Balance: %s %s",
                CommonUtils.convertWeiToTokenCurrency(AppProvider.get().getCurrentUser().getBalance()).toString(),
                AppProvider.get().getCurrentEconomy().getTokenSymbol()));

        mVerifyDataJson = (JSONObject) getVerifyData();

        ((TextView)viewGroup.findViewById(R.id.atv_transfer_type)).setText(mVerifyDataJson.optString(OstConstants.RULE_NAME).toUpperCase());

        LinearLayout transferHolder = ((LinearLayout)viewGroup.findViewById(R.id.ll_transfer_holder));

        JSONArray tokenHolderAddressesList = mVerifyDataJson.optJSONArray(OstConstants.TOKEN_HOLDER_ADDRESSES);
        JSONArray tokenHolderAmountsList = mVerifyDataJson.optJSONArray(OstConstants.AMOUNTS);
        for (int i=0; i<tokenHolderAddressesList.length(); i++) {
            String tokenHolderAddress = tokenHolderAddressesList.optString(i);
            String tokenHolderAmount = tokenHolderAmountsList.optString(i);
            LinearLayout layoutTransfer = new LinearLayout(getContext());
            layoutTransfer.setOrientation(LinearLayout.HORIZONTAL);
            layoutTransfer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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
            tokenHolderValueView.setText(
                    String.format("%s %s", CommonUtils.convertWeiToTokenCurrency(tokenHolderAmount),
                            AppProvider.get().getCurrentEconomy().getTokenSymbol())
            );
            tokenHolderValueView.setTextColor(Color.parseColor("#34445b"));
            tokenHolderValueView.setTextSize(14);


            layoutTransfer.addView(tokenHolderValueView);

            transferHolder.addView(layoutTransfer);
        }

        ((TextView)viewGroup.findViewById(R.id.tv_not_enough_balance)).setVisibility(View.GONE);
        return viewGroup;
    }

    String getTitle() {
        return "Authorize Transaction";
    }
}
