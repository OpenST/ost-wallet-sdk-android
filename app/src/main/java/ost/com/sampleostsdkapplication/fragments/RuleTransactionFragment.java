package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ost.walletsdk.OstSdk;

import java.util.Arrays;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the Create user session screen for OstDemoApp.
 */
public class RuleTransactionFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "RuleTransactionFragment";
    private String mUserId;

    private LinearLayout mExternalView;
    private SeekBar mAmountSlider;
    private TextView mTransferAmountView;
    private TextView mTokenHolderAddressView;
    private String mTokenHolderAddress;
    private Spinner mSpinnerRuleName;

    private String[] ruleType = { "BT", "USD"};

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.transaction_details_view, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);
        mAmountSlider = view.findViewById(R.id.amountSlider);
        mAmountSlider.setOnSeekBarChangeListener(this);

        mSpinnerRuleName = view.findViewById(R.id.spinnerRuleName);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, ruleType);
        mSpinnerRuleName.setAdapter(arrayAdapter);
        mTokenHolderAddressView = view.findViewById(R.id.tokenHolderAddressValue);
        mTokenHolderAddressView.setText(mTokenHolderAddress);

        mTransferAmountView = view.findViewById(R.id.transferAmount);
        mTransferAmountView.setText("2500");

        return view;
    }

    public String getPageTitle() {
        return getResources().getString(R.string.rule_transaction);
    }


    @Override
    public void onNextClick() {

        String transferType = (String)mSpinnerRuleName.getSelectedItem();
        String ruleName = OstSdk.RULE_NAME_DIRECT_TRANSFER; // set default value

        switch (transferType) {
            case "BT":
                ruleName = OstSdk.RULE_NAME_DIRECT_TRANSFER;
                break;
            case "USD":
                ruleName = OstSdk.RULE_NAME_DIRECT_TRANSFER;
                break;
        }

        OstSdk.executeTransaction(mUserId,
                Arrays.asList(mTokenHolderAddress),
                Arrays.asList(mTransferAmountView.getText().toString()),
                ruleName,
                this);

        super.onNextClick();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static RuleTransactionFragment newInstance(String currentUserId, String tokenHolderAddress) {
        RuleTransactionFragment fragment = new RuleTransactionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mUserId = currentUserId;
        fragment.mTokenHolderAddress = tokenHolderAddress;
        return fragment;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        long amountInWei = progress * 100;
        mTransferAmountView.setText(String.valueOf(amountInWei));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}