package com.ost.mobilesdk.workflow;

import android.graphics.Bitmap;

import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstAddDeviceFlowInterface;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstStartPollingInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWalletWordsAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

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

    @Override
    public void showPaperWallet(String[] mnemonicsArray) {

    }
}
