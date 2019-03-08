package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.security.UserPassphrase;

import ost.com.sampleostsdkapplication.R;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Fragment representing the Reset User Pin screen for OstDemoApp.
 */
public class DeviceRecoveryFragment extends BaseFragment {
    private String mUserId;
    private String mTokenId;
    private TextInputLayout mPassphraseTextInput;
    private EditText mEditTextPassphrase;
    private TextInputLayout mAddressToRecoverTextInput;
    private EditText mEditTextAddressToRecover;
    private LinearLayout mExternalView;
    private byte[] mAppSalt;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.two_input_fragment, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);

        ((TextView) view.findViewById(R.id.first_text_view)).setText("Input Pin");
        ((TextView) view.findViewById(R.id.second_text_view)).setText("Input RecoveryAddress");

        mPassphraseTextInput = view.findViewById(R.id.first_text_input);
        mEditTextPassphrase = view.findViewById(R.id.first_edit_box);

        mAddressToRecoverTextInput = view.findViewById(R.id.second_text_input);
        mAddressToRecoverTextInput.setHint("Input Recovery Address");
        mEditTextAddressToRecover = view.findViewById(R.id.second_edit_box);
        mEditTextAddressToRecover.setInputType(InputType.TYPE_CLASS_TEXT);
        return view;
    }

    public String getPageTitle() {
        return getResources().getString(R.string.device_recovery);
    }

    /**
     * Perform operation on clicking next
     */
    public void onNextClick() {
        if (mEditTextPassphrase.getText() == null || mEditTextPassphrase.getText().length() < 6) {
            mPassphraseTextInput.setError(getResources().getString(R.string.enter_six_digit_pin));
            return;
        }
        if (mEditTextAddressToRecover.getText() == null || mEditTextAddressToRecover.getText().length() < 40 || mEditTextAddressToRecover.getText().length() > 42) {
            mAddressToRecoverTextInput.setError(getResources().getString(R.string.enter_40_char_address));
            return;
        }
        showLoader();
        String currentPin = mEditTextPassphrase.getText().toString();
        String address = mEditTextAddressToRecover.getText().toString();
        UserPassphrase passphrase = new UserPassphrase(mUserId, currentPin.getBytes(UTF_8), mAppSalt);
        OstSdk.initiateRecoverDevice(mUserId, passphrase, address, this);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static DeviceRecoveryFragment newInstance(String tokenId, String userId, byte[] appSalt) {
        DeviceRecoveryFragment fragment = new DeviceRecoveryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        fragment.mAppSalt = appSalt;
        return fragment;
    }
}