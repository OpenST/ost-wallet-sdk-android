package ost.com.sampleostsdkapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.OstWorkflowContext;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWalletWordsAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

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
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Toast.makeText(OstSdk.getContext(), "Work Flow Successfull", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Toast.makeText(OstSdk.getContext(), String.format("Work Flow %s Error: %s", ostWorkflowContext.getWorkflow_type(), ostError.getMessage()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void determineAddDeviceWorkFlow(OstAddDeviceFlowInterface addDeviceFlowInterface) {
        addDeviceFlowInterface.QRCodeFlow();
    }

    @Override
    public void showQR(Bitmap qrImage, OstStartPollingInterface startPollingInterface) {
        Log.i(TAG, "showing QR code");
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        qrImage.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();

        Intent anotherIntent = new Intent(mApp, QR_view.class);
        anotherIntent.putExtra("image", byteArray);
        mApp.startActivity(anotherIntent);
        //Need to be on click of button "start polling"
        startPollingInterface.startPolling();
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

    @Override
    public void showPaperWallet(String[] mnemonicsArray) {

    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {

    }
}