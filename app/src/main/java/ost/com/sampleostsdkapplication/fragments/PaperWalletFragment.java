package ost.com.sampleostsdkapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
    private MaterialButton cancelButton;
    private EditText mPWEditBox;
    private RelativeLayout mActionButtons;
    private FrameLayout mActionLoaders;
    private TextView mWalletInstructionText;
    private OnPaperWalletFragmentListener mListener;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.paper_wallet_fragment, container, false);
        mPWEditBox = view.findViewById(R.id.paper_wallet_edit_box);
        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
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
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static PaperWalletFragment newInstance(String tokenId, String userId) {
        PaperWalletFragment fragment = new PaperWalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        return fragment;
    }

    /**
     * Perform operation on clicking next
     *
     * @param view
     */
    private void onNextClick(){
        mActionButtons.setVisibility(View.GONE);
        mActionLoaders.setVisibility(View.VISIBLE);
        mListener.onShowPaperWalletButton();
    }

    public void showWalletWords(String[] mnemonicsArray, String showText){
        mActionButtons.setVisibility(View.VISIBLE);
        mActionLoaders.setVisibility(View.GONE);
        if(mnemonicsArray != null){
            mPWEditBox.setText(TextUtils.join(",", mnemonicsArray));
        }
        if(showText != null){
            mWalletInstructionText.setText(showText);
            mWalletInstructionText.setVisibility(View.VISIBLE);
        }
    }

    public interface OnPaperWalletFragmentListener{
        void onBack();
        void onShowPaperWalletButton();
        void paperWalletFetchingDone(String[] walletWords, String showText);
    }
}
