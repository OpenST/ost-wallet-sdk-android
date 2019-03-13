package ost.com.sampleostsdkapplication;

import android.util.Log;
import android.widget.Toast;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.workflows.OstContextEntity;
import com.ost.mobilesdk.workflows.OstWorkflowContext;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.interfaces.OstDeviceRegisteredInterface;
import com.ost.mobilesdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.mobilesdk.workflows.interfaces.OstVerifyDataInterface;
import com.ost.mobilesdk.workflows.interfaces.OstWorkFlowCallback;

import org.json.JSONObject;

import java.util.ArrayList;

public class WorkFlowHelper implements OstWorkFlowCallback {


    private static final String TAG = "WorkFlowHelper";
    
    @Override
    public void registerDevice(JSONObject apiParams, OstDeviceRegisteredInterface ostDeviceRegisteredInterface) {

    }

    @Override
    public void getPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

    }

    @Override
    public void invalidPin(OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {

    }

    @Override
    public void pinValidated(OstWorkflowContext ostWorkflowContext, String userId) {

    }

    @Override
    public void flowComplete(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        Log.d("Workflow", "Inside workflow complete");
        Toast.makeText(OstSdk.getContext(), "Work Flow Successfull", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void flowInterrupt(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        Toast.makeText(OstSdk.getContext(), String.format("Work Flow %s Error: %s", ostWorkflowContext.getWorkflow_type(), ostError.getMessage()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestAcknowledged(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {

    }

    @Override
    public void verifyData(OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        ostVerifyDataInterface.dataVerified();
    }
}