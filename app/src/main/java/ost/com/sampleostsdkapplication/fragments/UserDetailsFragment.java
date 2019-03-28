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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;
import com.ost.walletsdk.workflows.interfaces.OstDeviceRegisteredInterface;

import org.json.JSONObject;

import java.util.List;

import ost.com.sampleostsdkapplication.R;
import ost.com.sampleostsdkapplication.UsersListActivity;
import ost.com.sampleostsdkapplication.WorkFlowHelper;

/**
 * Fragment representing the User Details for OstDemoApp.
 */
public class UserDetailsFragment extends Fragment {
    private static String TAG = "UserDetailsFragment";

    private String mUserId;
    private String mTokenId;
    private EditText mUserIdEdit;
    private EditText mUserStatusEdit;
    private EditText mTokenIdEdit;
    private EditText mUserTHEdit;
    private EditText mUserDMEdit;
    private EditText mUserRecoveryOwnerAddressEdit;
    private EditText mDeviceAddrEdit;
    private EditText mDeviceStatusEdit;
    private Button mSyncUserButton;
    private ProgressBar mProgressView;
    private LinearLayout mUserDetailPage;
    private EditText mUserRecoveryAddressEdit;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_detail_fragment, container, false);
        mUserDetailPage = view.findViewById(R.id.user_detail_page);
        mProgressView = view.findViewById(R.id.progress_bar_sync);
        mUserIdEdit = view.findViewById(R.id.ost_user_id_edit);
        mUserStatusEdit = view.findViewById(R.id.ost_user_status_edit);
        mTokenIdEdit = view.findViewById(R.id.ost_token_id_edit);
        mUserTHEdit = view.findViewById(R.id.ost_user_token_holder_edit);
        mUserDMEdit = view.findViewById(R.id.ost_user_device_manager_edit);
        mUserRecoveryOwnerAddressEdit = view.findViewById(R.id.ost_user_recovery_owner_address_edit);
        mUserRecoveryAddressEdit = view.findViewById(R.id.ost_user_recover_address_edit);
        mDeviceAddrEdit = view.findViewById(R.id.ost_user_device_address_edit);
        mDeviceStatusEdit = view.findViewById(R.id.ost_user_device_status_edit);
        mSyncUserButton = view.findViewById(R.id.btn_sync_user);
        mSyncUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncUserDetails();
            }
        });
        return view;
    }

    final WorkFlowHelper workFlowHelper = new WorkFlowHelper() {
        @Override
        public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {

            // The device is in created state.
            // Sdk has asked Test-App to register it.
            // As this fragment is only shown when user-is logged in,
            // This could mean that:
            // Either user has deleted data, which deletes keys
            // OR
            // Device Key that we assumed was present with Sdk is no longer valid (revoked or deleted).
            //

            // For Test-App use-case, lets call cancelFlow and show what ever we can.
            // Ideally, app should logout if they believe this is an unexpected callback.
            // Note: Calling cancelFlow will trigger flowInterrupt callback.
            ostDeviceRegisteredInterface.cancelFlow();

            // Show the Existing user and device details.
            showProgress(false);
            populateData();
        }

        @Override
        public void flowComplete(OstWorkflowContext workflowContext, OstContextEntity ostContextEntity) {
            super.flowComplete(workflowContext, ostContextEntity);
            populateData();
            showProgress(false);
        }

        @Override
        public void flowInterrupt(OstWorkflowContext workflowContext, OstError ostError) {
            super.flowInterrupt(workflowContext, ostError);
            populateData();
            showProgress(false);

            //region Sdk Error Handling
            if ( OstErrors.ErrorCode.WORKFLOW_CANCELLED == ostError.getErrorCode() ) {
                //Test-App has cancelled the workflow because it got register device callback.
                deviceUnauthorized(ostError);
            } else if ( ostError.isApiError() ) {
                OstApiError apiError = (OstApiError) ostError;
                if ( apiError.isApiSignerUnauthorized() ) {
                    // The device has been revoked and can not make any more calls to OST Platform.
                    // Apps must logout users at this point.
                    // For purpose of testing the sdk, lets give users an option.
                    deviceUnauthorized(apiError);
                } else {
                    //Let's log the error
                    logSdkError( workflowContext, ostError );
                }
            } else if (OstErrors.ErrorCode.DEVICE_NOT_SETUP == ostError.getErrorCode() ) {
                // Device needs to be registered or new device keys need to be created.
                // To perform this operation, Test-App needs to call OstSdk.setupDevice()
                // If app believe that user is authenticated, they should logout user here.
                deviceUnauthorized(ostError);
            } else {
                //Let's log the error
                logSdkError( workflowContext, ostError );
            }
            //endregion
        }
    };

    private String logSdkError(OstWorkflowContext ostWorkflowContext, OstError ostError) {
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

            List<OstApiError.ApiErrorData> apiErrorDataList = ostApiError.getErrorData();
            for (OstApiError.ApiErrorData apiErrorData : apiErrorDataList) {
                String errorData = String.format(
                        "\n%s: %s",
                        apiErrorData.getParameter(),
                        apiErrorData.getMsg());

                errorStringBuilder.append(errorData);
            }
        }
        String stringFormattedError = errorStringBuilder.toString();
        Log.e(TAG, stringFormattedError);
        return stringFormattedError;
    }

    private void syncUserDetails() {
        showProgress(true);

        OstSdk.setupDevice(mUserId, mTokenId, true, workFlowHelper);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateData();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static UserDetailsFragment newInstance(String tokenId, String userId) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        return fragment;
    }

    /**
     * Populate Data in the fragment view.
     */
    private void populateData() {
        OstUser user = OstSdk.getUser(mUserId);
        OstDevice device = user.getCurrentDevice();
        mUserIdEdit.setText(mUserId);
        mUserStatusEdit.setText(user.getStatus());
        mTokenIdEdit.setText(mTokenId);
        mUserTHEdit.setText(user.getTokenHolderAddress());
        mUserDMEdit.setText(user.getDeviceManagerAddress());
        mUserRecoveryOwnerAddressEdit.setText(user.getRecoveryOwnerAddress());
        mUserRecoveryAddressEdit.setText(user.getRecoveryAddress());
        if (null != device) {
            mDeviceAddrEdit.setText(device.getAddress());
            mDeviceStatusEdit.setText(device.getStatus());
        }
    }

    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mUserDetailPage.setVisibility(show ? View.GONE : View.VISIBLE);
        mUserDetailPage.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mUserDetailPage.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    void deviceUnauthorized(OstError ostError) {
        String title = "Device not registered";
        String message = "Please login again to register your device.";
        if (ostError.isApiError()) {
            OstApiError apiError = (OstApiError) ostError;
            if ( apiError.isApiSignerUnauthorized() ) {
                title = "Device Revoked";
            }
        }
        UsersListActivity activity = (UsersListActivity) getActivity();
        activity.showLogoutDialog(title, message);

    }

}
