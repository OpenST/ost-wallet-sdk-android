package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the SetUp User screen for OstDemoApp.
 */
public class SetUpUserFragment extends BaseFragment {
    private String mUserId;
    private String mTokenId;
    private TextInputLayout mPinTextInput;
    private EditText mPinEditBox;
    private LinearLayout mExternalView;

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

    public String getPageTitle(){
        return getResources().getString(R.string.setup_wallet);
    }

    /**
     * Perform operation on clicking next
     */
    public void onNextClick(){
        if (mPinEditBox.getText() == null || mPinEditBox.getText().length() < 6){
            mPinTextInput.setError(getResources().getString(R.string.enter_six_digit_pin));
            return;
        }
        showLoader();
        OnSetUpUserFragmentListener mListener = (OnSetUpUserFragmentListener) getFragmentListener();
        mListener.onSetupUserSubmit(mPinEditBox.getText().toString());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static SetUpUserFragment newInstance(String tokenId, String userId) {
        SetUpUserFragment fragment = new SetUpUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        return fragment;
    }

    public interface OnSetUpUserFragmentListener extends OnBaseFragmentListener{
        void onSetupUserSubmit(String pin);
    }
}
