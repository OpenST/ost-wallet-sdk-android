package ost.com.sampleostsdkapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the login screen for OstDemoApp.
 */
public class SetUpUserFragment extends Fragment implements View.OnClickListener {

    private String mUserId;
    private String mTokenId;
    private MaterialButton nextButton;
    private MaterialButton cancelButton;
    private TextInputLayout mPinTextInput;
    private EditText mPinEditBox;
    private RelativeLayout mActionButtons;
    private FrameLayout mActionLoaders;
    private OnSetUpUserFragmentListener mListener;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.setup_user_fragment, container, false);
        mPinTextInput = view.findViewById(R.id.user_setup_pin);
        mPinEditBox = view.findViewById(R.id.user_setup_pin_edit);
        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        mActionButtons = view.findViewById(R.id.action_buttons);
        mActionLoaders = view.findViewById(R.id.action_loader);

//        createAccountButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mLoginViewController.onButtonAction(mUserNameEditText.getText(), mNumberEditText.getText(), true);
//            }
//        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        if(mListener != null){
            mListener = null;
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSetUpUserFragmentListener) {
            mListener = (OnSetUpUserFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChannelSelectionFragmentListener");
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.cancel_button: {
                mListener.onBack();
                break;
            }
            case R.id.next_button: {
                onNextClick(  );
                break;
            }
        }
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

    /**
     * Perform operation on clicking next
     * @param view
     */
    private void onNextClick(){
        if (mPinEditBox.getText() == null || mPinEditBox.getText().length() != 6){
            mPinTextInput.setError("Please enter 6 Digit Pin");
            return;
        }
        mActionButtons.setVisibility(View.GONE);
        mActionLoaders.setVisibility(View.VISIBLE);
        mListener.onSetupUserSubmit(mPinEditBox.getText().toString());
    }

    public interface OnSetUpUserFragmentListener{
        void onBack();
        void onSetupUserSubmit(String pin);
    }
}
