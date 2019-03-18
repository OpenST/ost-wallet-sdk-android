package ost.com.sampleostsdkapplication.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import ost.com.sampleostsdkapplication.R;
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
    private EditText mUserRecoveryKeyEdit;
    private EditText mDeviceAddrEdit;
    private EditText mDeviceNameEdit;
    private EditText mDeviceStatusEdit;
    private Button mSyncUserButton;
    private ProgressBar mProgressView;
    private LinearLayout mUserDetailPage;

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
        mUserRecoveryKeyEdit = view.findViewById(R.id.ost_user_recovery_key_edit);
        mDeviceAddrEdit = view.findViewById(R.id.ost_user_device_address_edit);
        mDeviceNameEdit = view.findViewById(R.id.ost_user_device_name_edit);
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
        public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
            super.flowComplete(ostWorkflowContext, ostContextEntity);
            populateData();
            showProgress(false);
        }

        @Override
        public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
            super.flowInterrupt(ostWorkflowContext, ostError);
            populateData();
            showProgress(false);
        }
    };

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
        mUserRecoveryKeyEdit.setText(user.getRecoveryOwnerAddress());
        if (null != device) {
            mDeviceAddrEdit.setText(device.getAddress());
            mDeviceNameEdit.setText(device.getDeviceName());
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
}
