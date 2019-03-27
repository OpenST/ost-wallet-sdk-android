package ost.com.sampleostsdkapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import ost.com.sampleostsdkapplication.R;

/**
 * Fragment representing the Create user session screen for OstDemoApp.
 */
public class QRPerformFragment extends BaseFragment {
    private String mUserId;
    private String mTokenId;

    private LinearLayout mExternalView;
    private TextView mVerifyDataView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View childLayout = inflater.inflate(R.layout.qr_data_view, null);
        mExternalView = view.findViewById(R.id.external_view);
        mExternalView.addView(childLayout);
        mVerifyDataView = view.findViewById(R.id.verifyDataView);

        return view;
    }

    public String getPageTitle() {
        return getResources().getString(R.string.qr_perform);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static QRPerformFragment newInstance(String tokenId, String userId) {
        QRPerformFragment fragment = new QRPerformFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mTokenId = tokenId;
        fragment.mUserId = userId;
        return fragment;
    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, final OstVerifyDataInterface ostVerifyDataInterface) {
        super.verifyData(ostWorkflowContext, ostContextEntity, ostVerifyDataInterface);
        JSONObject jsonObject;
        String dataToVerify = null;
        if (OstSdk.DEVICE.equalsIgnoreCase(ostContextEntity.getEntityType())) {
            OstDevice ostDevice = ((OstDevice) ostContextEntity.getEntity());
            if (OstWorkflowContext.WORKFLOW_TYPE.REVOKE_DEVICE_WITH_QR_CODE.equals(
                    ostWorkflowContext.getWorkflow_type()
            )) {
                dataToVerify = createRevokeDeviceString(ostDevice);
            } else {
                dataToVerify = createAuthorizeDeviceString(ostDevice);
            }
        } else {
            jsonObject = (JSONObject) ostContextEntity.getEntity();
            dataToVerify = createTransactionString(jsonObject);
        }

        mVerifyDataView.setText(dataToVerify);
        getNextButton().setText(getString(R.string.authorize));
        getNextButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ostVerifyDataInterface.dataVerified();
                getNextButton().setEnabled(false);
                showLoader();
            }
        });
    }

    private String createTransactionString(JSONObject jsonObject) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Rule Name: ");
        stringBuilder.append(
                jsonObject.optString(OstConstants.RULE_NAME)
        );

        JSONArray tokenHolderAddressesList = jsonObject.optJSONArray(OstConstants.TOKEN_HOLDER_ADDRESSES);
        JSONArray tokenHolderAmountsList = jsonObject.optJSONArray(OstConstants.AMOUNTS);
        for (int i=0; i<tokenHolderAddressesList.length(); i++) {
            String tokenHolderAddress = tokenHolderAddressesList.optString(i);
            String tokenHolderAmount = tokenHolderAmountsList.optString(i);

            stringBuilder.append("\nToken Holder Address: ");
            stringBuilder.append(tokenHolderAddress);

            stringBuilder.append("\nToken Holder Amount: ");
            stringBuilder.append(tokenHolderAmount);
        }
        return stringBuilder.toString();
    }

    private String createRevokeDeviceString(OstDevice ostDevice) {
        return "Device Address To Add: " +
                ostDevice.getAddress();
    }

    private String createAuthorizeDeviceString(OstDevice ostDevice) {
        return "Device Address To Revoke: " +
                ostDevice.getAddress();
    }
}