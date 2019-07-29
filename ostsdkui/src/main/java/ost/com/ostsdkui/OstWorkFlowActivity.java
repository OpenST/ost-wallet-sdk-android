package ost.com.ostsdkui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import ost.com.ostsdkui.managedevices.Device;
import ost.com.ostsdkui.managedevices.DeviceListFragment;
import ost.com.ostsdkui.managedevices.DeviceListRecyclerViewAdapter;
import ost.com.ostsdkui.recovery.AbortRecoveryFragment;
import ost.com.ostsdkui.recovery.InitiateRecoveryFragment;
import ost.com.ostsdkui.recovery.RecoveryFragment;
import ost.com.ostsdkui.sdkInteract.SdkInteract;
import ost.com.ostsdkui.sdkInteract.WorkFlowListener;
import ost.com.ostsdkui.util.FragmentUtils;
import ost.com.ostsdkui.util.KeyBoard;
import ost.com.ostsdkui.walletsetup.WalletSetUpFragment;

import static ost.com.ostsdkui.recovery.RecoveryFragment.DEVICE_ADDRESS;

public class OstWorkFlowActivity extends BaseActivity implements WalletSetUpFragment.OnFragmentInteractionListener,
        DeviceListRecyclerViewAdapter.OnDeviceListInteractionListener {

    public static final String WORKFLOW_ID = "workflowId";
    public static final String WORKFLOW_NAME = "workflowName";
    public static final String ACTIVATE_USER = "activate_user";
    public static final String USER_ID = "user_id";
    public static final String EXPIRED_AFTER_SECS = "expired_after_secs";
    public static final String SPENDING_LIMIT = "spending_limit";
    public static final String INITIATE_RECOVERY = "initiate_recovery";
    public static final String ABORT_RECOVERY = "abort_recovery";

    private static final String LOG_TAG = "OstWorkFlowActivity";
    private WorkFlowListener mWorkFlowListener;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ost_work_flow);
        mIntent = getIntent();
        String workflowId = getIntent().getStringExtra(WORKFLOW_ID);
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
            } else if (INITIATE_RECOVERY.equalsIgnoreCase(workflow)) {
                String deviceAddress = getIntent().getStringExtra(DEVICE_ADDRESS);
                if (TextUtils.isEmpty(deviceAddress)) {
                    FragmentUtils.addFragment(R.id.layout_container,
                            DeviceListFragment.initiateRecoveryInstance(getIntent().getExtras()),
                            this);
                } else {
                    FragmentUtils.addFragment(R.id.layout_container,
                            InitiateRecoveryFragment.newInstance(getIntent().getExtras()),
                            this);
                }
            } else if (ABORT_RECOVERY.equalsIgnoreCase(workflow)) {
                FragmentUtils.addFragment(R.id.layout_container,
                        AbortRecoveryFragment.newInstance(getIntent().getExtras()),
                        this);
            }
        }
    }

    @Override
    protected View getRootView() {
        return findViewById(R.id.layout_container);
    }


    @Override
    public void goBack() {
        Fragment topFragment = FragmentUtils.getTopFragment(this, R.id.layout_container);
        boolean consumed = false;
        if (topFragment instanceof ChildFragmentStack) {
            consumed = ((ChildFragmentStack)topFragment).popBack();
        }
        if (!consumed) {
            Fragment fragment = FragmentUtils.getTopFragment(this, R.id.layout_container);
            if (!(fragment instanceof WalletSetUpFragment || fragment instanceof RecoveryFragment ||
                    fragment instanceof DeviceListFragment)) {
                FragmentUtils.goBack(this);
            } else {
                //hide keyboard if open
                KeyBoard.hideKeyboard(OstWorkFlowActivity.this);
                super.goBack();
            }
        }
    }

    @Override
    public void activateAcknowledged(String workflowId) {
        finish();
//        FragmentUtils.goBack(this);
    }

    @Override
    public void openWebView(String url) {
        WebViewFragment fragment = WebViewFragment.newInstance(url);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void onDeviceSelectToRevoke(Device device) {

    }

    @Override
    public void onDeviceSelectedForRecovery(Device device) {
        Bundle bundle = mIntent.getExtras();
        bundle.putString(DEVICE_ADDRESS, device.getDeviceAddress());
        FragmentUtils.addFragment(R.id.layout_container,
                InitiateRecoveryFragment.newInstance(bundle),
                this);
    }

    @Override
    public void onDeviceSelectedToAbortRecovery(Device device) {
        Bundle bundle = mIntent.getExtras();
        bundle.putString(DEVICE_ADDRESS, device.getDeviceAddress());
        FragmentUtils.addFragment(R.id.layout_container,
                AbortRecoveryFragment.newInstance(bundle),
                this);
    }
}
