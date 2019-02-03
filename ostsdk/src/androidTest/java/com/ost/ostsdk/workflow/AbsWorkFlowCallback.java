package com.ost.ostsdk.workflow;

import android.graphics.Bitmap;

import com.ost.ostsdk.workflows.OstContextEntity;
import com.ost.ostsdk.workflows.OstError;
import com.ost.ostsdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.ostsdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.ostsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.ostsdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.ostsdk.workflows.interfaces.OstWalletWordsAcceptInterface;
import com.ost.ostsdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

public class AbsWorkFlowCallback implements OstWorkFlowCallback {
    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {

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

    }

    @Override
    public void flowInterrupt(OstError ostError) {

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
