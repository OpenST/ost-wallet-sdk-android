package ost.com.sampleostsdkapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWalletWordsAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

class WorkFlowHelper implements OstWorkFlowCallback {


    private static final String TAG = "WorkFlowHelper";
    private final App mApp;

    WorkFlowHelper(Context context) {
        mApp = ((App) context.getApplicationContext());
    }

    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {
        Log.i(TAG, String.format("Device Object %s ", apiParams.toString()));
        String mUserId = mApp.getLoggedUser().getId();
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
    public void showQR(Bitmap qrImage, OstStartPollingInterface startPollingInterface) {

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