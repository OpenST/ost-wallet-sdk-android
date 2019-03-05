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
 * Fragment representing the Reset User Pin screen for OstDemoApp.
 */
public class ResetPinFragment extends BaseFragment {
    private String mUserId;
    private String mTokenId;
    private TextInputLayout mOldPinTextInput;
    private EditText mOldPinEditBox;
    private TextInputLayout mNewPinTextInput;
    private EditText mNewPinEditBox;
    private LinearLayout mExternalView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.reset_pin_fragment, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);
        mOldPinTextInput = view.findViewById(R.id.old_pin);
        mOldPinEditBox = view.findViewById(R.id.old_pin_edit_box);
        mNewPinTextInput = view.findViewById(R.id.new_pin);
        mNewPinEditBox = view.findViewById(R.id.new_pin_edit_box);
        return view;
    }

    public String getPageTitle(){
        return getResources().getString(R.string.reset_pin);
    }

    /**
     * Perform operation on clicking next
     * @param view
     */
    public void onNextClick(){
        if (mOldPinEditBox.getText() == null || mOldPinEditBox.getText().length() != 6){
            mOldPinTextInput.setError(getResources().getString(R.string.enter_six_digit_pin));
            return;
        }
        if (mNewPinEditBox.getText() == null || mNewPinEditBox.getText().length() != 6){
            mNewPinTextInput.setError(getResources().getString(R.string.enter_six_digit_pin));
            return;
        }
        if (mNewPinEditBox.getText().toString().equals(mOldPinEditBox.getText().toString())){
            mNewPinTextInput.setError(getResources().getString(R.string.new_old_pin_same));
            return;
        }
        showLoader();
        OnResetPinFragmentListener mListener = (OnResetPinFragmentListener) getFragmentListener();
        mListener.onResetPinSubmit(mOldPinEditBox.getText().toString(), mNewPinEditBox.getText().toString());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static ResetPinFragment newInstance(String tokenId, String userId) {
        ResetPinFragment fragment = new ResetPinFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        return fragment;
    }

    public interface OnResetPinFragmentListener extends OnBaseFragmentListener{
        void onResetPinSubmit(String OldPin, String NewPin);
    }
}
