package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the login screen for OstDemoApp.
 */
public class PaperWalletFragment extends BaseFragment {

    private String mUserId;
    private String mTokenId;
    private MaterialButton nextButton;
    private EditText mPWEditBox;
    private Boolean inAuthorizeDeviceMode;
    private LinearLayout mExternalView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.paper_wallet_fragment, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);
        nextButton = view.findViewById(R.id.next_button);
        if(inAuthorizeDeviceMode){
            nextButton.setText("Authorize");
        }
        mPWEditBox = view.findViewById(R.id.paper_wallet_edit_box);
        return view;
    }

    public String getPageTitle(){
        return getResources().getString(R.string.paper_wallet);
    }

    /**
     * Perform operation on clicking next
     * @param view
     */
    public void onNextClick(){
        if(inAuthorizeDeviceMode){
            if(mPWEditBox.getText() == null){
                return;
            }
            String mnemonicsText = mPWEditBox.getText().toString();
            String[] splits = mnemonicsText.split(" ");
            if( splits.length != 12){
                showWalletInstructionText("Invalid Mnemonics String. It should be 12 space seperated words");
                return;
            }
            showLoader();
            OnPaperWalletFragmentListener mListener = (OnPaperWalletFragmentListener) getFragmentListener();
            mListener.authorizeDeviceUsingMnemonics(mnemonicsText, mUserId);
        } else {
            showLoader();
            OnPaperWalletFragmentListener mListener = (OnPaperWalletFragmentListener) getFragmentListener();
            mListener.onShowPaperWalletButton();
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

    public void showWalletWords(String mnemonics, String showText) {
        hideLoader();
        if (mnemonics != null) {
            mPWEditBox.setText(mnemonics);
        }
        showWalletInstructionText(showText);
    }

    public interface OnPaperWalletFragmentListener extends OnBaseFragmentListener{
        void onShowPaperWalletButton();
        void paperWalletFetchingDone(String mnemonics, String showText);
        void authorizeDeviceUsingMnemonics(String mnemonicsText, String userId);
    }
}
