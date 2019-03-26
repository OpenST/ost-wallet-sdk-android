package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.transaction_details_view, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);
        mAmountSlider = view.findViewById(R.id.amountSlider);
        mAmountSlider.setOnSeekBarChangeListener(this);

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

        if (TextUtils.isEmpty(mTokenHolderAddress)) {
            Log.e(TAG, "Token Holder Address is Empty");
            return;
        }

        OstSdk.executeTransaction(mUserId,
                Arrays.asList(mTokenHolderAddress),
                Arrays.asList(mTransferAmountView.getText().toString()),
                OstSdk.RULE_NAME_DIRECT_TRANSFER,
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