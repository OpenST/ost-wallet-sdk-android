/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.util;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ost.walletsdk.R;

public class FragmentUtils {

    public static boolean isBackStackEmpty(FragmentActivity fragmentAct) {
        FragmentManager fm = fragmentAct.getSupportFragmentManager();
        return fm.getBackStackEntryCount() == 0;
    }

    public static void addFragment(int contId, Fragment fg,
                                   FragmentActivity fragmentAct) {
        addFragment(contId, fg, fragmentAct, null);
    }

    public static void addFragment(int contId, Fragment fg,
                                   FragmentActivity fragmentAct, String tag) {
        KeyBoard.hideKeyboard(fragmentAct);
        FragmentTransaction transaction = fragmentAct
                .getSupportFragmentManager().beginTransaction();
        transaction.add(contId, fg, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    public static void addFragmentWithoutBackStack(int contId, Fragment fg,
                                                   FragmentActivity fragmentAct) {
        KeyBoard.hideKeyboard(fragmentAct);
        FragmentTransaction transaction = fragmentAct
                .getSupportFragmentManager().beginTransaction();
        transaction.add(contId, fg);
        transaction.commit();
    }

    public static void clearBackStackAndAddFragment(int contId, Fragment fg,
                                                    FragmentActivity fragmentAct) {
        clearBackStack(fragmentAct);
        addFragment(contId, fg, fragmentAct);
    }

    public static void clearBackStackAndAddFragmentWithoutIt(
            int contId, Fragment fg, FragmentActivity fragmentAct) {
        addFragmentWithoutBackStack(contId, fg, fragmentAct);
    }

    public static void clearBackStack(FragmentActivity fragmentAct) {
        FragmentManager manager = fragmentAct.getSupportFragmentManager();
        int backStackCount = manager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = manager.getBackStackEntryAt(i).getId();
            manager.popBackStack(backStackId,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public static void replaceWithoutBackStack(int contId, Fragment fg,
                                               FragmentActivity fragmentAct) {
        FragmentTransaction transaction = fragmentAct
                .getSupportFragmentManager().beginTransaction();
        transaction.replace(contId, fg);
        transaction.commit();
    }

    public static void replaceWithoutBackStackWithTransition(int contId,
                                                             Fragment fg, FragmentActivity fragmentAct) {
        FragmentTransaction tr = fragmentAct
                .getSupportFragmentManager().beginTransaction();
        tr.setCustomAnimations(android.R.anim.slide_in_left, 0);
        tr.replace(contId, fg);
        tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        tr.commit();
    }

    public static void replaceWithoutBackStackWithTransition(int contId,
                                                             int animId, Fragment fg, FragmentActivity fragmentAct) {
        FragmentTransaction tr = fragmentAct
                .getSupportFragmentManager().beginTransaction();
        tr.setCustomAnimations(animId, 0);
        tr.replace(contId, fg);
        tr.commit();
    }

    public static void replaceWithBackStackWithTransition(int contId,
                                                          int animId, Fragment fg, FragmentActivity fragmentAct) {
        FragmentTransaction tr = fragmentAct
                .getSupportFragmentManager().beginTransaction();
        tr.setCustomAnimations(animId, 0);
        tr.replace(contId, fg);
        tr.addToBackStack(null);
        tr.commit();
    }

    public static void replaceWithBackStack(int contId, Fragment fg, FragmentActivity fragmentAct) {
        FragmentTransaction tr = fragmentAct
                .getSupportFragmentManager().beginTransaction();
        tr.replace(contId, fg);
        tr.addToBackStack(null);
        tr.commit();
    }

    public static Fragment getTopFragment(FragmentActivity fragmentAct, int contId) {
        return fragmentAct.getSupportFragmentManager().findFragmentById(contId);
    }

    public static Fragment getFragmentByTag(FragmentActivity fragmentAct, String tag) {
        return fragmentAct.getSupportFragmentManager().findFragmentByTag(tag);
    }

    public static void goBack(FragmentActivity fragmentAct) {
        KeyBoard.hideKeyboard(fragmentAct);
        FragmentTransaction transaction = fragmentAct.getSupportFragmentManager().beginTransaction();
        transaction.remove(FragmentUtils.getTopFragment(fragmentAct, R.id.layout_container));
        transaction.commit();
        fragmentAct.getSupportFragmentManager().popBackStack();
    }
}