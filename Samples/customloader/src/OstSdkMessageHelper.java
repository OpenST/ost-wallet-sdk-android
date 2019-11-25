package customloader.src;

import android.content.Context;
import android.text.TextUtils;

import com.ost.walletsdk.network.OstApiError;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OstSdkMessageHelper {

    private final static String DEFAULT_CONTEXT = "__DEFAULT_CONTEXT";
    private static final boolean DEVELOPER_MODE = false;
    private static final String SUCCESS_MSG_KEY = "SUCCESS_MESSAGE";
    private static JSONObject ALL_ERRORS = new JSONObject();
    private final String DEVICE_OUT_OF_SYNC = "DEVICE_OUT_OF_SYNC";
    private final String DEFAULT_ERROR_MSG = "Something went wrong";
    private static Map<String, String> BASE_ERROR_MSG = new HashMap<>();

    private static void init(Context context) {
        InputStream configInputStream = null;
        try {
            configInputStream = context.getAssets().open("OstSdkMessages.json");
            int size = configInputStream.available();
            byte[] buffer = new byte[size];

            configInputStream.read(buffer);
            configInputStream.close();

            String json = new String(buffer, "UTF-8");
            ALL_ERRORS = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getErrorMessage(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        String errMsg = _getErrorMessage(ostWorkflowContext, ostError);
        return errMsg;
    }

    private String _getErrorMessage(OstWorkflowContext ostWorkflowContext, OstError ostError) {
        String errMsg = null;

        if (null == ostError) {
            return DEFAULT_ERROR_MSG;
        }

        String errorCode = ostError.getErrorCode().toString();

        String workflowType = null != ostWorkflowContext ? ostWorkflowContext.getWorkflowType().toString() : null;
        if (null == workflowType) workflowType = DEFAULT_CONTEXT;

        if (ostError.isApiError() && ((OstApiError)ostError).isDeviceTimeOutOfSync()) {
            errorCode = DEVICE_OUT_OF_SYNC;

            if (null != ALL_ERRORS.optJSONObject(workflowType)) {
                errMsg = ALL_ERRORS.optJSONObject(workflowType).optString(errorCode);
            }

            if (null == errMsg) {
                errMsg = ALL_ERRORS.optJSONObject(DEFAULT_CONTEXT).optString(errorCode);
            }

            if (DEVELOPER_MODE) {
                errMsg = String.format("%s\n\n(%s)", errMsg, ((OstApiError) ostError).getApiInternalId());
            }

            if (null == errMsg) errMsg = DEFAULT_ERROR_MSG;

            return errMsg;
        }
        if (ostError.isApiError() && ((OstApiError)ostError).isApiSignerUnauthorized()) {
            if (TextUtils.isEmpty(errMsg)) {
                errMsg = ALL_ERRORS.optJSONObject(DEFAULT_CONTEXT).optString(errorCode);
            }
            if (null == errMsg) errMsg = DEFAULT_ERROR_MSG;

            return errMsg;
        }
        if (ostError.isApiError()) {
            OstApiError ostApiError = (OstApiError) ostError;
            if (TextUtils.isEmpty(errMsg)) {
                List<OstApiError.ApiErrorData> apiErrorDataList = ostApiError.getErrorData();
                if (null != apiErrorDataList && apiErrorDataList.size() > 0) {
                    OstApiError.ApiErrorData apiErrorData = apiErrorDataList.get(0);
                    errMsg = apiErrorData.getMsg();
                    if (null == errMsg) errMsg = DEFAULT_ERROR_MSG;
                }else {
                    errMsg = ostApiError.getErrMsg();
                }
            }

            if (null == errMsg) errMsg =  DEFAULT_ERROR_MSG;
            return errMsg;
        }

        if ( TextUtils.isEmpty(errorCode) ) {
            return DEFAULT_ERROR_MSG;
        }

        if ( ALL_ERRORS.has(workflowType) ) {
            errMsg = ALL_ERRORS.optJSONObject(workflowType).optString(errorCode);
        }

        if ( TextUtils.isEmpty(errMsg) ) {
            errMsg = ALL_ERRORS.optJSONObject(DEFAULT_CONTEXT).optString(errorCode);
        }

        if ( DEVELOPER_MODE && !TextUtils.isEmpty(errorCode)) {
            if ( TextUtils.isEmpty(errMsg) ) {
                if (null == errMsg) errMsg =  DEFAULT_ERROR_MSG;
            }

            errMsg = errMsg + "\n\n (" + errorCode + "," + ostError.getInternalErrorCode() + ")";
        }

        if (TextUtils.isEmpty(errMsg)) errMsg =  DEFAULT_ERROR_MSG;
        return errMsg;
    }

    public String getSuccessText(OstWorkflowContext ostWorkflowContext) {
        String successText = "Success";

        OstWorkflowContext.WORKFLOW_TYPE type = ostWorkflowContext.getWorkflowType();
        JSONObject jsonObject = ALL_ERRORS.optJSONObject(type.toString());

        if (null != jsonObject) {
            successText = jsonObject.optString(SUCCESS_MSG_KEY, "Success");
        }

        return successText;
    }

    OstSdkMessageHelper(Context context) {
        if (0 == ALL_ERRORS.length()) init(context);
    }
}