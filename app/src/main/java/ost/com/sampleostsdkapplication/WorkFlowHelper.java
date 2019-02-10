package ost.com.sampleostsdkapplication;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWalletWordsAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONException;
import org.json.JSONObject;

class WorkFlowHelper implements OstWorkFlowCallback {


    private static final String TAG = "WorkFlowHelper";

    WorkFlowHelper() {

    }

    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
        Log.i(TAG, String.format("Device Object %s ", apiParams.toString()));
        String mUserId = null;
        try {
            mUserId = apiParams.getJSONObject(OstSdk.DEVICE).getString(OstDevice.USER_ID);
        } catch (JSONException e) {
            Log.e(TAG, String.format("Error While getting param %s", OstDevice.USER_ID));
            ostDeviceRegisteredInterface.cancelFlow(new OstError(String.format("Error While getting param %s", OstDevice.USER_ID)));
        }
        new MappyApiClient().registerDevice(mUserId, apiParams, new MappyApiClient.Callback() {
            @Override
            public void onResponse(boolean success, JSONObject response) {
                if (success) {
                    ostDeviceRegisteredInterface.deviceRegistered(response);
                } else {
                    ostDeviceRegisteredInterface.cancelFlow(new OstError(response.toString()));
                }
            }
        });
    }

    @Override
    public void getPin(String userId, OstPinAcceptInterface ostPinAcceptInterface) {

    }

    @Override
    public void invalidPin(String userId, OstPinAcceptInterface ostPinAcceptInterface) {

    }

    @Override
    public void pinValidated(String userId) {

    }

    @Override
    public void flowComplete(OstContextEntity ostContextEntity) {
        Toast.makeText(OstSdk.getContext(), "Work Flow Successfull", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void flowInterrupt(OstError ostError) {
        Toast.makeText(OstSdk.getContext(), "Work Flow Error:" + ostError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void determineAddDeviceWorkFlow(OstAddDeviceFlowInterface addDeviceFlowInterface) {

    }

    @Override
    public void showQR(OstStartPollingInterface startPollingInterface, Bitmap qrImage) {

    }

    @Override
    public void getWalletWords(OstWalletWordsAcceptInterface ostWalletWordsAcceptInterface) {

    }

    @Override
    public void invalidWalletWords(OstWalletWordsAcceptInterface ostWalletWordsAcceptInterface) {

    }

    @Override
    public void walletWordsValidated() {

    }

    @Override
    public void deviceUnauthorized() {

    }
}