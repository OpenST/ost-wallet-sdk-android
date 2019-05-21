package ost.com.demoapp.ui.workflow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

        ((TextView)viewGroup.findViewById(R.id.atv_transfer_type)).setText(mVerifyDataJson.optString(OstConstants.RULE_NAME));

        LinearLayout transferHolder = ((LinearLayout)viewGroup.findViewById(R.id.ll_transfer_holder));

        JSONArray tokenHolderAddressesList = mVerifyDataJson.optJSONArray(OstConstants.TOKEN_HOLDER_ADDRESSES);
        JSONArray tokenHolderAmountsList = mVerifyDataJson.optJSONArray(OstConstants.AMOUNTS);
        for (int i=0; i<tokenHolderAddressesList.length(); i++) {
            String tokenHolderAddress = tokenHolderAddressesList.optString(i);
            String tokenHolderAmount = tokenHolderAmountsList.optString(i);
            RelativeLayout layoutTransfer = new RelativeLayout(getContext());
            layoutTransfer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

            OstTextView tokenHolderAddressView = new OstTextView(getContext());
            RelativeLayout.LayoutParams paramsTokenHolder = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsTokenHolder.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            tokenHolderAddressView.setLayoutParams(paramsTokenHolder);
            tokenHolderAddressView.setText(tokenHolderAddress);
            tokenHolderAddressView.setTextColor(Color.parseColor("#34445b"));
            tokenHolderAddressView.setTextSize(13);

            layoutTransfer.addView(tokenHolderAddressView);

            OstBoldTextView tokenHolderValueView = new OstBoldTextView(getContext());
            RelativeLayout.LayoutParams paramsTokenHolderValue = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsTokenHolderValue.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            tokenHolderValueView.setLayoutParams(paramsTokenHolderValue);
            tokenHolderValueView.setText(tokenHolderAmount);
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
