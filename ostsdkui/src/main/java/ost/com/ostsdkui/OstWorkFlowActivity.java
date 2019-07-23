package ost.com.ostsdkui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ost.com.ostsdkui.sdkInteract.SdkInteract;
import ost.com.ostsdkui.sdkInteract.WorkFlowListener;
import ost.com.ostsdkui.util.FragmentUtils;
import ost.com.ostsdkui.walletsetup.WalletSetUpFragment;

public class OstWorkFlowActivity extends BaseActivity implements WalletSetUpFragment.OnFragmentInteractionListener {

    public static final String WORKFLOW_ID = "workflowId";
    public static final String WORKFLOW_NAME = "workflowName";
    public static final String ACTIVATE_USER = "activate_user";
    public static final String USER_ID = "user_id";
    public static final String EXPIRED_AFTER_SECS = "expired_after_secs";
    public static final String SPENDING_LIMIT = "spending_limit";

    private static final String LOG_TAG = "OstWorkFlowActivity";
    private WorkFlowListener mWorkFlowListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ost_work_flow);
        long workflowId = getIntent().getLongExtra(WORKFLOW_ID, -1);
        String workflow = getIntent().getStringExtra(WORKFLOW_NAME);
        String userId = getIntent().getStringExtra(USER_ID);
        mWorkFlowListener = SdkInteract.getInstance().getWorkFlowListener(workflowId);
        if (null == mWorkFlowListener || null == workflow) {
            Log.e(LOG_TAG,"Work flow is null");
            finish();
        } else {
            if (ACTIVATE_USER.equalsIgnoreCase(workflow)) {
                FragmentUtils.addFragment(R.id.layout_container,
                        WalletSetUpFragment.newInstance(getIntent().getExtras()),
                        this);
            }
        }
    }

    @Override
    protected View getRootView() {
        return findViewById(R.id.layout_container);
    }

    @Override
    public void activateAcknowledged(long workflowId) {
        finish();
//        FragmentUtils.goBack(this);
    }

    @Override
    public void openWebView(String url) {
//        WebViewFragment fragment = WebViewFragment.newInstance(url);
//        FragmentUtils.addFragment(R.id.layout_container,
//                fragment,
//                this);
    }
}
