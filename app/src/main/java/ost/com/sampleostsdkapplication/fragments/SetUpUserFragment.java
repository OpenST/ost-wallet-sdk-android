package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.UserPassphrase;
import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the SetUp User screen for OstDemoApp.
 */
public class SetUpUserFragment extends BaseFragment {
    private static final String TAG = "SetUpUserFragment";
    private String mUserId;
    private String mTokenId;
    private TextInputLayout mPinTextInput;
    private EditText mPinEditBox;
    private LinearLayout mExternalView;
    private String mPassPhrasePrefix;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.setup_user_fragment, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);
        mPinTextInput = view.findViewById(R.id.user_setup_pin);
        mPinEditBox = view.findViewById(R.id.user_setup_pin_edit);
        return view;
    }

    public String getPageTitle() {
        return getResources().getString(R.string.setup_wallet);
    }

    /**
     * Perform operation on clicking next
     */
    public void onNextClick() {
        if (mPinEditBox.getText() == null || mPinEditBox.getText().length() < 6) {
            mPinTextInput.setError(getResources().getString(R.string.enter_six_digit_pin));
            return;
        }

        Log.d(TAG, "Start user activation process");
        long expiresAfterInSecs = 2 * 7 * 24 * 60 * 60; //2 weeks
        String spendingLimit = "1000000000000";


        OstSdk.activateUser(
                new UserPassphrase(mUserId, mPinEditBox.getText().toString(), mPassPhrasePrefix),
                expiresAfterInSecs,
                spendingLimit,
                this
        );

        super.onNextClick();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static SetUpUserFragment newInstance(String tokenId, String userId, String passphrasePrefix) {
        SetUpUserFragment fragment = new SetUpUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        fragment.mPassPhrasePrefix = passphrasePrefix;
        return fragment;
    }
}
