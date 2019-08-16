/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.recovery;


import android.app.Fragment;
import android.os.Bundle;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.util.ChildFragmentUtils;
import com.ost.walletsdk.ui.walletsetup.PinFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InitiateRecoveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InitiateRecoveryFragment extends RecoveryFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static InitiateRecoveryFragment newInstance(Bundle bundle) {
        InitiateRecoveryFragment fragment = new InitiateRecoveryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public RecoveryPresenter getPresenter() {
        return InitiateRecoveryPresenter.getInstance();
    }

    @Override
    public void showEnterPin() {
        PinFragment fragment = PinFragment.newInstance("Recover Access to Your Wallet",
                "Enter your 6-digit PIN to recover access to your wallet",  "", mShowBackButton);
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container, fragment, this);

        fragment.contentConfig = ContentConfig.getInstance()
                .getStringConfig("initiate_recovery")
                .optJSONObject("get_pin");
    }
}