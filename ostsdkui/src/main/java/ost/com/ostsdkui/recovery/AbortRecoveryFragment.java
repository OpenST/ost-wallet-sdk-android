/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.ostsdkui.recovery;


import android.app.Fragment;
import android.os.Bundle;

import ost.com.ostsdkui.R;
import ost.com.ostsdkui.util.ChildFragmentUtils;
import ost.com.ostsdkui.walletsetup.PinFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AbortRecoveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AbortRecoveryFragment extends RecoveryFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     */
    public static AbortRecoveryFragment newInstance(Bundle bundle) {
        AbortRecoveryFragment fragment = new AbortRecoveryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public RecoveryPresenter getPresenter() {
        return AbortRecoveryPresenter.getInstance();
    }

    @Override
    public void showEnterPin() {
        ChildFragmentUtils.clearBackStackAndAddFragment(R.id.layout_container,
                PinFragment.newInstance("Abort Recovery",
                        "Enter your 6-digit PIN to abort recovery"),
                this);
    }
}