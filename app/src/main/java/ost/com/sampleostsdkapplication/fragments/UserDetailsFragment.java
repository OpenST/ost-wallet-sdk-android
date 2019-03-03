package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.models.entities.OstUser;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the User Details for OstDemoApp.
 */
public class UserDetailsFragment extends Fragment {

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

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_detail_fragment, container, false);
        mUserIdEdit = view.findViewById(R.id.ost_user_id_edit);
        mUserStatusEdit = view.findViewById(R.id.ost_user_status_edit);
        mTokenIdEdit = view.findViewById(R.id.ost_token_id_edit);
        mUserTHEdit = view.findViewById(R.id.ost_user_token_holder_edit);
        mUserDMEdit = view.findViewById(R.id.ost_user_device_manager_edit);
        mUserRecoveryKeyEdit = view.findViewById(R.id.ost_user_recovery_key_edit);
        mDeviceAddrEdit = view.findViewById(R.id.ost_user_device_address_edit);
        mDeviceNameEdit = view.findViewById(R.id.ost_user_device_name_edit);
        mDeviceStatusEdit = view.findViewById(R.id.ost_user_device_status_edit);
        return view;
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
     *
     */
    private void populateData(){
        OstUser user = OstSdk.getUser(mUserId);
        OstDevice device = user.getCurrentDevice();
        mUserIdEdit.setText(mUserId);
        mUserStatusEdit.setText(user.getStatus());
        mTokenIdEdit.setText(mTokenId);
        mUserTHEdit.setText(user.getTokenHolderAddress());
        mUserDMEdit.setText(user.getDeviceManagerAddress());
        mUserRecoveryKeyEdit.setText(user.getRecoveryOwnerAddress());
        mDeviceAddrEdit.setText(device.getAddress());
        mDeviceNameEdit.setText(device.getDeviceName());
        mDeviceStatusEdit.setText(device.getStatus());
    }
}
