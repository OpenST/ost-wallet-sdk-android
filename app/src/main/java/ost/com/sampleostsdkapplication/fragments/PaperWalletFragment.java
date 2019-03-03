package ost.com.sampleostsdkapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the login screen for OstDemoApp.
 */
public class PaperWalletFragment extends Fragment implements View.OnClickListener {

    private String mUserId;
    private String mTokenId;
    private MaterialButton nextButton;
    private MaterialButton authorizeButton;
    private MaterialButton cancelButton;
    private EditText mPWEditBox;
    private RelativeLayout mActionButtons;
    private FrameLayout mActionLoaders;
    private TextView mWalletInstructionText;
    private Boolean inAuthorizeDeviceMode;
    private OnPaperWalletFragmentListener mListener;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.paper_wallet_fragment, container, false);
        mPWEditBox = view.findViewById(R.id.paper_wallet_edit_box);
        authorizeButton = view.findViewById(R.id.authorize_device);
        nextButton = view.findViewById(R.id.next_button);
        if(inAuthorizeDeviceMode){
            nextButton.setVisibility(View.GONE);
            authorizeButton.setVisibility(View.VISIBLE);
            authorizeButton.setOnClickListener(this);
        } else {
            authorizeButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setOnClickListener(this);
        }
        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        mActionButtons = view.findViewById(R.id.action_buttons);
        mActionLoaders = view.findViewById(R.id.action_loader);
        mWalletInstructionText = view.findViewById(R.id.wallet_instruction_text);
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
        if (context instanceof OnPaperWalletFragmentListener) {
            mListener = (OnPaperWalletFragmentListener) context;
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
            case R.id.authorize_device: {
                onAuthorizeClick();
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
    public static PaperWalletFragment newInstance(String tokenId, String userId, boolean inAuthorizeDeviceMode) {
        PaperWalletFragment fragment = new PaperWalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        fragment.inAuthorizeDeviceMode = inAuthorizeDeviceMode;
        return fragment;
    }

    /**
     * Perform operation on clicking next
     *
     */
    private void onNextClick(){
        mActionButtons.setVisibility(View.GONE);
        mActionLoaders.setVisibility(View.VISIBLE);
        mListener.onShowPaperWalletButton();
    }

    private void onAuthorizeClick(){
        if(mPWEditBox.getText() == null){
            return;
        }
        String mnemonicsText = mPWEditBox.getText().toString();
        String[] splits = mnemonicsText.split(" ");
        if( splits.length != 12){
            mWalletInstructionText.setText("Invalid Mnemonics String. It should be 12 space seperated words");
            mWalletInstructionText.setVisibility(View.VISIBLE);
            return;
        }
        mActionButtons.setVisibility(View.GONE);
        mActionLoaders.setVisibility(View.VISIBLE);
        mListener.authorizeDeviceUsingMnemonics(mnemonicsText, mUserId);
    }

    public void showWalletWords(String mnemonics, String showText) {
        mActionButtons.setVisibility(View.VISIBLE);
        mActionLoaders.setVisibility(View.GONE);
        if (mnemonics != null) {
            mPWEditBox.setText(mnemonics);
        }
        if(showText != null){
            mWalletInstructionText.setText(showText);
            mWalletInstructionText.setVisibility(View.VISIBLE);
        }
    }

    public interface OnPaperWalletFragmentListener{
        void onBack();
        void onShowPaperWalletButton();

        void paperWalletFetchingDone(String mnemonics, String showText);
        void authorizeDeviceUsingMnemonics(String mnemonicsText, String userId);
    }
}
