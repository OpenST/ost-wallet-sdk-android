package ost.com.sampleostsdkapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the Base of all fragments.
 */
public class BaseFragment extends Fragment implements View.OnClickListener {

    private String mUserId;
    private String mTokenId;
    private MaterialButton nextButton;
    private MaterialButton cancelButton;
    private RelativeLayout mActionButtons;
    private FrameLayout mActionLoaders;
    private TextView mWalletInstructionText;
    private OnBaseFragmentListener mListener;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout of common base fragment
        View view = inflater.inflate(R.layout.common_base_fragment, container, false);
        TextView pageTitle = view.findViewById(R.id.page_title);
        pageTitle.setText(getPageTitle());
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBaseFragmentListener) {
            mListener = (OnBaseFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBaseFragmentListener");
        }
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

    public OnBaseFragmentListener getFragmentListener(){
        return mListener;
    }

    public String getPageTitle(){
        return "";
    }

    public void onNextClick(){}

    public void showLoader(){
        mActionButtons.setVisibility(View.GONE);
        mActionLoaders.setVisibility(View.VISIBLE);
    }

    public void hideLoader(){
        mActionButtons.setVisibility(View.VISIBLE);
        mActionLoaders.setVisibility(View.GONE);
    }

    public void showWalletInstructionText(String showText){
        if(showText != null){
            mWalletInstructionText.setText(showText);
            mWalletInstructionText.setVisibility(View.VISIBLE);
        }
    }

    public interface OnBaseFragmentListener{
        void onBack();
    }
}
