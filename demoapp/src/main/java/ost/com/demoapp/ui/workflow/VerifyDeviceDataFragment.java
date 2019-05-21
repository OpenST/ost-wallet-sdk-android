package ost.com.demoapp.ui.workflow;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.ost.walletsdk.models.entities.OstDevice;

import ost.com.demoapp.R;
import ost.com.demoapp.uicomponents.OstTextView;

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

    @Override
    View getVerifyDataView() {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.view_verify_device, null);

        ((OstTextView)viewGroup.findViewById(R.id.lbl_verify_data_heading)).setText(getVerifyDataHeading());
        ((OstTextView)viewGroup.findViewById(R.id.atv_verify_data)).setText(createAuthorizeDeviceString((OstDevice)getVerifyData()));

        return viewGroup;
    }

    private String createAuthorizeDeviceString(OstDevice verifyData) {
        if (null == verifyData) {
            return null;
        }
        return  ((OstDevice)getVerifyData()).getAddress();
    }
}
