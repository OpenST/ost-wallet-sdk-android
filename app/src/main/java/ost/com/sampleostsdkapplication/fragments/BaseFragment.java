package ost.com.sampleostsdkapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ost.mobilesdk.security.UserPassphrase;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.OstWorkflowContext;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;

import java.util.Arrays;

import ost.com.sampleostsdkapplication.LogInUser;
import ost.com.sampleostsdkapplication.R;
import ost.com.sampleostsdkapplication.UsersListActivity;
import ost.com.sampleostsdkapplication.WorkFlowHelper;

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
    private View mView;
    private TextInputLayout mWorkflowDetails;
    private EditText mWorkflowDetailsBox;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout of common base fragment
        mView = inflater.inflate(R.layout.common_base_fragment, container, false);
        TextView pageTitle = mView.findViewById(R.id.page_title);
        pageTitle.setText(getPageTitle());
        nextButton = mView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
        cancelButton = mView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        mActionButtons = mView.findViewById(R.id.action_buttons);
        mActionLoaders = mView.findViewById(R.id.action_loader);
        mWalletInstructionText = mView.findViewById(R.id.wallet_instruction_text);
        mWorkflowDetails = mView.findViewById(R.id.workflow_details);
        mWorkflowDetailsBox = mView.findViewById(R.id.workflow_details_box);
        return mView;
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

    /**
     * Method to register workflow callbacks on views.
     *
     * @param logInUser
     * @return
     */
    public WorkFlowHelper registerWorkflowCallbacks(){
        WorkFlowHelper wfh = new WorkFlowHelper(getActivity()) {
            @Override
            public void getPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
                super.getPin(ostWorkflowContext, userId, ostPinAcceptInterface);
                UsersListActivity activity = (UsersListActivity) getActivity();
                activity.showPinDialog(ostPinAcceptInterface);
            }

            @Override
            public void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
                super.invalidPin(ostWorkflowContext, userId, ostPinAcceptInterface);
                hideLoader();
                showWalletInstructionText("Invalid Pin.");
            }

            @Override
            public void showPaperWallet(byte[] mnemonics) {
                super.showPaperWallet(mnemonics);
                showWalletWords(new String(mnemonics), "Please save these words carefully.");
            }

            @Override
            public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
                Log.d("Workflow", "Inside workflow complete");
                super.flowComplete(ostWorkflowContext, ostContextEntity);
                addWorkflowTaskText("Workflow completed at: ");
                hideLoader();
            }

            @Override
            public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
                Log.d("Workflow", "Inside workflow acknowledged");
                super.requestAcknowledged(ostWorkflowContext, ostContextEntity);
                addWorkflowTaskText("Workflow acknowledged at: ");
            }

            @Override
            public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
                Log.d("Workflow", "Inside workflow interrupt");
                addWorkflowTaskText("Workflow interrupted at: ");
                super.flowInterrupt(ostWorkflowContext, ostError);
                hideLoader();
            }
        };
        addWorkflowTaskText("Workflow Initiated at: ");
        return wfh;
    }

    public void showWalletWords(String mnemonics, String showText) {
        hideLoader();
        if (mnemonics != null) {
            EditText mPWEditBox = mView.findViewById(R.id.paper_wallet_edit_box);
            mPWEditBox.setText(mnemonics);
        }
        showWalletInstructionText(showText);
    }

    public void addWorkflowTaskText(String str){
        String finalStr = mWorkflowDetailsBox.getText().toString();
        finalStr += ("\n " + str + String.valueOf((int) (System.currentTimeMillis() / 1000)));
        mWorkflowDetailsBox.setText(finalStr);
        mWorkflowDetails.setVisibility(View.VISIBLE);
    }

    public interface OnBaseFragmentListener{
        void onBack();
    }
}
