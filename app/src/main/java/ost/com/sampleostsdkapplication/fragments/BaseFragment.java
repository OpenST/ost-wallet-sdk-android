/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.sampleostsdkapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.network.OstApiError.ApiErrorData;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.walletsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.util.List;

import ost.com.sampleostsdkapplication.R;
import ost.com.sampleostsdkapplication.UsersListActivity;

/**
 * Fragment representing the Base of all fragments.
 */
public class BaseFragment extends Fragment implements View.OnClickListener, OstWorkFlowCallback {

    private static final String TAG = "OstBaseFragment";
    private ImageView mImageOst;

    MaterialButton getNextButton() {
        return nextButton;
    }
    MaterialButton getCancelButton() {
        return cancelButton;
    }

    private MaterialButton cancelButton;
    private MaterialButton nextButton;
    private RelativeLayout mActionButtons;
    private FrameLayout mActionLoaders;
    private TextView mWalletInstructionText;
    private OnBaseFragmentListener mListener;
    private View mView;
    private TextView mWorkflowDetailsBox;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout of common base fragment
        mView = inflater.inflate(R.layout.common_base_fragment, container, false);
        TextView pageTitle = mView.findViewById(R.id.page_title);
        pageTitle.setText(getPageTitle());
        cancelButton = mView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        nextButton = mView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
        mActionButtons = mView.findViewById(R.id.action_buttons);
        mActionLoaders = mView.findViewById(R.id.action_loader);
        mWalletInstructionText = mView.findViewById(R.id.wallet_instruction_text);
        mWorkflowDetailsBox = mView.findViewById(R.id.workflow_details_box);

        mImageOst = mView.findViewById(R.id.image_ost);
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
    public void onDestroy() {
        if (mListener != null) {
            mListener = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_button: {
                mListener.onBack();
                break;
            }
            case R.id.next_button: {
                onNextClick();
                break;
            }
        }
    }

    public OnBaseFragmentListener getFragmentListener() {
        return mListener;
    }

    public String getPageTitle() {
        return "";
    }

    public void onNextClick() {
        flowStarted();
        showLoader();
    }

    public void showLoader() {
        if (null == mActionButtons) return;

        mActionButtons.setVisibility(View.GONE);
        mActionLoaders.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        if (null == mActionButtons) return;

        mActionButtons.setVisibility(View.VISIBLE);
        mActionLoaders.setVisibility(View.GONE);
    }

    public void showWalletInstructionText(String showText) {
        if (showText != null) {
            mWalletInstructionText.setText(showText);
            mWalletInstructionText.setVisibility(View.VISIBLE);
        }
    }

    public void flowStarted() {
        addWorkflowTaskText("Work flow started at: ");
    }

    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {

    }

    @Override
    public void getPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        UsersListActivity activity = (UsersListActivity) getActivity();
        if (null == activity) {
            Log.e(TAG, "In get Pin activity is null");
            ostPinAcceptInterface.cancelFlow();
            return;
        }
        activity.showPinDialog(ostPinAcceptInterface);
    }

    @Override
    public void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        showWalletInstructionText("Invalid Pin.");
        UsersListActivity activity = (UsersListActivity) getActivity();
        if (null == activity) {
            Log.e(TAG, "In invalid Pin activity is null");
            ostPinAcceptInterface.cancelFlow();
            return;
        }
        activity.showPinDialog(ostPinAcceptInterface);
    }

    @Override
    public void pinValidated(OstWorkflowContext ostWorkflowContext, String userId) {

    }


    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        String completeString = String.format("Workflow %s complete entity %s ",
                ostWorkflowContext.getWorkflow_type(), null == ostContextEntity ? "null" : ostContextEntity.getEntityType());

        Log.d("Workflow", "Inside workflow complete");
        Toast.makeText(OstSdk.getContext(), "Work Flow Successful", Toast.LENGTH_SHORT).show();

        Log.d("Workflow", "Inside workflow complete");
        addWorkflowTaskText(String.format("%s completed at: ", completeString));
        hideLoader();
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d("Workflow", "Inside workflow acknowledged");
        addWorkflowTaskText(String.format("Entity type: %s\n Workflow acknowledged at: ",
                null == ostContextEntity ? "null" : ostContextEntity.getEntityType()));
    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        Log.d("Workflow", "Inside workflow verify Data");
        addWorkflowTaskText(String.format("Verify data: %s", (null == ostContextEntity ? new JSONObject() : ostContextEntity.getEntity()).toString()));
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        StringBuilder errorStringBuilder = new StringBuilder();

        String errorString = String.format("Work Flow %s " +
                        "\nError: %s " +
                        "\nwith error code: %s" +
                        "\ninternal error code: %s",
                ostWorkflowContext.getWorkflow_type(),
                ostError.getMessage(),
                ostError.getErrorCode(),
                ostError.getInternalErrorCode()
        );

        Toast.makeText(OstSdk.getContext(), errorString, Toast.LENGTH_SHORT).show();

        errorStringBuilder.append(errorString);

        if (ostError.isApiError()) {
            OstApiError ostApiError = ((OstApiError)ostError);

            String apiErrorCodeMsg = String.format(
                    "\n%s: %s",
                    ostApiError.getErrCode(),
                    ostApiError.getErrMsg());

            errorStringBuilder.append(apiErrorCodeMsg);

            errorStringBuilder.append(
                    String.format("\napi_internal_id: %s", ostApiError.getApiInternalId())
            );

            List<ApiErrorData> apiErrorDataList = ostApiError.getErrorData();
            for (ApiErrorData apiErrorData : apiErrorDataList) {
                String errorData = String.format(
                        "\n%s: %s",
                        apiErrorData.getParameter(),
                        apiErrorData.getMsg());

                errorStringBuilder.append(errorData);
            }
        }

        Log.d("Workflow", "Inside workflow interrupt");
        addWorkflowTaskText(String.format("%s interrupted at: ", errorStringBuilder.toString() ));
        hideLoader();
    }

    public void showWalletWords(String mnemonics, String showText) {
        hideLoader();
        if (mnemonics != null) {
            EditText mPWEditBox = mView.findViewById(R.id.paper_wallet_edit_box);
            mPWEditBox.setText(mnemonics);
        }
        showWalletInstructionText(showText);
    }

    public void addWorkflowTaskText(String str) {

        if (null == mWorkflowDetailsBox) return;

        String finalStr = mWorkflowDetailsBox.getText().toString();
        finalStr += ("\n " + str + String.valueOf((int) (System.currentTimeMillis() / 1000)));
        mWorkflowDetailsBox.setText(finalStr);
        mWorkflowDetailsBox.scrollTo(0, mWorkflowDetailsBox.getBottom());
        mWorkflowDetailsBox.setVisibility(View.VISIBLE);
    }

    public ImageView getOstImage() {
        return mImageOst;
    }

    public interface OnBaseFragmentListener {
        void onBack();
    }
}
