package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.ost.walletsdk.OstSdk;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the Create user session screen for OstDemoApp.
 */
public class RuleTransactionFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "RuleTransactionFragment";
    private String mUserId;

    private LinearLayout mExternalView;
    private SeekBar mAmountSlider;
    private TextInputEditText mTransferAmountView;
    private TextInputEditText mTokenHolderAddressView;
    private TextInputEditText mDescriptionEditText;

    private String mTokenHolderAddress;
    private Spinner mSpinnerRuleName;
    private Spinner mSpinnerUnit;


    private String[] ruleType = { "BT", "USD"};
    private String[] unitType = {  "WEI", "ETH"};

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.transaction_details_view, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);

        getOstImage().setVisibility(View.GONE);

        mAmountSlider = view.findViewById(R.id.amountSlider);
        mAmountSlider.setOnSeekBarChangeListener(this);

        mDescriptionEditText = view.findViewById(R.id.descriptionEditText);
        mSpinnerRuleName = view.findViewById(R.id.spinnerRuleName);
        ArrayAdapter ruleArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, ruleType);
        mSpinnerRuleName.setAdapter(ruleArrayAdapter);

        mSpinnerUnit = view.findViewById(R.id.spinnerUnit);
        ArrayAdapter unitArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, unitType);
        mSpinnerUnit.setAdapter(unitArrayAdapter);

        mTokenHolderAddressView = view.findViewById(R.id.tokenHolderAddressValue);
        mTokenHolderAddressView.setText(mTokenHolderAddress);

        mTransferAmountView = view.findViewById(R.id.transferAmount);
        mTransferAmountView.setText("150");

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

        String transferAmount = mTransferAmountView.getText().toString();
        BigInteger transferAmountBI = new BigInteger(transferAmount);
        String unitType = (String)mSpinnerUnit.getSelectedItem();

        switch (unitType) {
            case "ETH":
                BigInteger pow = new BigInteger("10").pow(18);
                transferAmountBI = transferAmountBI.multiply(pow);
                break;
            case "WEI":
                break;
        }

        Map<String, Object> map = getMeta();
        OstSdk.executeTransaction(mUserId,
                Arrays.asList(mTokenHolderAddress),
                Arrays.asList(transferAmountBI.toString()),
                ruleName,
                map,
                this);

        super.onNextClick();
    }

    private Map<String, Object> getMeta() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "known_user");
        map.put("type", "user_to_user");
        map.put("details", mDescriptionEditText.getText().toString());
        return map;
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
        long amountInWei = progress;
        mTransferAmountView.setText(String.valueOf(amountInWei));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}