package ost.com.demoapp.ui.workflow;

import android.os.Bundle;

public class VerifyDeviceDataFragment extends WorkFlowVerifyDataFragment {

    public static VerifyDeviceDataFragment newInstance() {
        VerifyDeviceDataFragment fragment = new VerifyDeviceDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    String getVerifyDataHeading() {
        return "Device Address";
    }

    String getPositiveButtonText() {
        return "Authorize Device";
    }

    String getSubHeading() {
        return "You’ve a new device authorization\n request from the following device";
    }

    String getTitle() {
        return "Authorize New Device";
    }
}
