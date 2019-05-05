/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.models.entities.OstDevice;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.workflows.OstContextEntity;
import com.ost.walletsdk.workflows.OstWorkflowContext;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.interfaces.OstPinAcceptInterface;
import com.ost.walletsdk.workflows.interfaces.OstVerifyDataInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.entity.User;
import ost.com.demoapp.network.MappyNetworkClient;
import ost.com.demoapp.sdkInteract.SdkInteract;
import ost.com.demoapp.sdkInteract.WorkFlowListener;
import ost.com.demoapp.ui.BaseActivity;
import ost.com.demoapp.ui.managedevices.DeviceListFragment;
import ost.com.demoapp.ui.workflow.WorkFlowPinFragment;
import ost.com.demoapp.ui.workflow.WorkFlowVerifyDataFragment;
import ost.com.demoapp.ui.workflow.transactions.TransactionFragment;
import ost.com.demoapp.ui.workflow.walletsetup.WalletSetUpFragment;
import ost.com.demoapp.util.FragmentUtils;

public class DashboardActivity extends BaseActivity implements
        TabLayout.OnTabSelectedListener,
        SdkInteract.FlowComplete,
        SdkInteract.FlowInterrupt,
        SdkInteract.PinCallback,
        SdkInteract.VerifyDataCallback,
        UserListFragment.OnListFragmentInteractionListener,
        WalletSetUpFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        WorkFlowPinFragment.OnFragmentInteractionListener,
        TransactionFragment.OnFragmentInteractionListener,
        DeviceListFragment.OnListFragmentInteractionListener {

    private static final String LOG_TAG = "DashboardActivity";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);

        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        homePagerAdapter.addFragment(UserListFragment.newInstance(), "Users");
        homePagerAdapter.addFragment(WalletFragment.newInstance(), "Wallet");
        homePagerAdapter.addFragment(SettingsFragment.newInstance(), "Wallet Settings");

        mViewPager.setAdapter(homePagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.home_navigation);
        mTabLayout.setTabTextColors(Color.parseColor("#9b9b9b"), getResources().getColor(R.color.colorPrimary));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(this);
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(R.drawable.users_icon);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setIcon(R.drawable.wallet_icon);
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setIcon(R.drawable.settings_icon);

        mViewPager.setCurrentItem(1);

        //Set SdkInteract Pin and verify data listeners
        SdkInteract.getInstance().setPinCallbackListener(this);
        SdkInteract.getInstance().setVerifyDataCallbackListener(this);

        checkForActivateUser();
    }

    private void checkForActivateUser() {
        OstUser ostUser = AppProvider.get().getCurrentUser().getOstUser();
        if (!(ostUser.isActivated() || ostUser.isActivating())) {
            FragmentUtils.addFragmentWithoutBackStack(R.id.layout_container,
                    WalletSetUpFragment.newInstance(),
                    this);
        }
    }

    @Override
    public void goBack() {
        if (!FragmentUtils.isBackStackEmpty(this)) {
            FragmentUtils.goBack(this);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tab.getIcon().setTint(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        tab.getIcon().setTintList(null);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void activateAcknowledged(long workflowId) {
        FragmentUtils.goBack(this);
        SdkInteract.getInstance().subscribe(
                workflowId,
                this
        );

        SdkInteract.getInstance().subscribe(
                workflowId,
                this
        );
    }

    @Override
    public void flowComplete(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity) {
        if (OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER
                .equals(
                        ostWorkflowContext.getWorkflow_type()
                )){
            AppProvider.get().getMappyClient().notifyUserActivate(new MappyNetworkClient.ResponseCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Log.d(LOG_TAG,"Activate User Sync Succeded");
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.d(LOG_TAG,"Activate User Sync Failed");
                }
            });
        }
    }

    @Override
    public void flowInterrupt(long workflowId, OstWorkflowContext ostWorkflowContext, OstError ostError) {
        if (OstWorkflowContext.WORKFLOW_TYPE.ACTIVATE_USER
                .equals(
                        ostWorkflowContext.getWorkflow_type()
                )){
            Log.e(LOG_TAG, "User Activate failed");
            checkForActivateUser();
        }
    }

    @Override
    public void launchFeatureFragment(Fragment fragment) {
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void getPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        showProgress(false);
        WorkFlowPinFragment fragment = WorkFlowPinFragment.newInstance("Get Pin");
        fragment.setPinCallback(ostPinAcceptInterface);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void invalidPin(long workflowId, OstWorkflowContext ostWorkflowContext, String userId, OstPinAcceptInterface ostPinAcceptInterface) {
        showProgress(false);
        WorkFlowPinFragment fragment = WorkFlowPinFragment.newInstance("Invalid Pin Try Again");
        fragment.setPinCallback(ostPinAcceptInterface);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void pinValidated(long workflowId, OstWorkflowContext ostWorkflowContext, String userId) {

    }

    @Override
    public void verifyData(long workflowId, OstWorkflowContext ostWorkflowContext, OstContextEntity ostContextEntity, OstVerifyDataInterface ostVerifyDataInterface) {
        showProgress(false);
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
        WorkFlowVerifyDataFragment fragment = WorkFlowVerifyDataFragment.newInstance();
        fragment.setDataToVerify(dataToVerify);
        fragment.setVerifyDataCallback(ostVerifyDataInterface);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
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
        return "Device Address To Revoke: " +
                ostDevice.getAddress();
    }

    private String createAuthorizeDeviceString(OstDevice ostDevice) {
        return "Device Address To Authorize: " +
                ostDevice.getAddress();
    }

    @Override
    public void popTopFragment() {
        FragmentUtils.goBack(this);
    }

    @Override
    public void onListFragmentInteraction(User user) {
        Fragment fragment = TransactionFragment.newInstance(user);
        FragmentUtils.addFragment(R.id.layout_container,
                fragment,
                this);
    }

    @Override
    public void onListFragmentInteraction(OstDevice device) {
        WorkFlowListener revokeDeviceWorkflowListener = SdkInteract.getInstance().newWorkFlowListener();

        SdkInteract.getInstance().subscribe(revokeDeviceWorkflowListener.getId(), this);

        if (OstDevice.CONST_STATUS.AUTHORIZED
                .equalsIgnoreCase(
                        device.getStatus()
                )) {
            OstSdk.revokeDevice(
                    AppProvider.get().getCurrentUser().getOstUserId(),
                    device.getAddress(),
                    revokeDeviceWorkflowListener
            );
        } else if (OstDevice.CONST_STATUS.RECOVERING
                .equalsIgnoreCase(
                        device.getStatus()
                        )) {
                //Todo:: abort recovery flow
        }
    }
}