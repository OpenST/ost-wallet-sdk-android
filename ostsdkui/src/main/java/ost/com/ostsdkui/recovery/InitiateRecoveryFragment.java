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
}