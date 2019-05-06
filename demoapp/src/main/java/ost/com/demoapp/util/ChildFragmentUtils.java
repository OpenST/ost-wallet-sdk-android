/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.util;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import ost.com.demoapp.R;

public class ChildFragmentUtils {

    public static boolean isBackStackEmpty(Fragment fragmentAct) {
        FragmentManager fm = fragmentAct.getChildFragmentManager();
        return fm.getBackStackEntryCount() == 0;
    }

    public static void addFragment(int contId, Fragment fg,
                                   Fragment fragmentAct) {
        KeyBoard.hideKeyboard(fragmentAct.getContext());
        FragmentTransaction transaction = fragmentAct
                .getChildFragmentManager().beginTransaction();
        transaction.add(contId, fg);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void addFragmentWithoutBackStack(int contId, Fragment fg,
                                                   Fragment fragmentAct) {
        KeyBoard.hideKeyboard(fragmentAct.getContext());
        FragmentTransaction transaction = fragmentAct
                .getChildFragmentManager().beginTransaction();
        transaction.add(contId, fg);
        transaction.commit();
    }

    public static void clearBackStackAndAddFragment(int contId, Fragment fg,
                                                    Fragment fragmentAct) {
        clearBackStack(fragmentAct);
        addFragment(contId, fg, fragmentAct);
    }

    public static void clearBackStackAndAddFragmentWithoutIt(
            int contId, Fragment fg, Fragment fragmentAct) {
        addFragmentWithoutBackStack(contId, fg, fragmentAct);
    }

    public static void clearBackStack(Fragment fragmentAct) {
        FragmentManager manager = fragmentAct.getChildFragmentManager();
        int backStackCount = manager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId = manager.getBackStackEntryAt(i).getId();
            manager.popBackStack(backStackId,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public static void replaceWithoutBackStack(int contId, Fragment fg,
                                               Fragment fragmentAct) {
        FragmentTransaction transaction = fragmentAct
                .getChildFragmentManager().beginTransaction();
        transaction.replace(contId, fg);
        transaction.commit();
    }

    public static void replaceWithoutBackStackWithTransition(int contId,
                                                             Fragment fg, Fragment fragmentAct) {
        FragmentTransaction tr = fragmentAct
                .getChildFragmentManager().beginTransaction();
        tr.setCustomAnimations(android.R.anim.slide_in_left, 0);
        tr.replace(contId, fg);
        tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        tr.commit();
    }

    public static void replaceWithoutBackStackWithTransition(int contId,
                                                             int animId, Fragment fg, Fragment fragmentAct) {
        FragmentTransaction tr = fragmentAct
                .getChildFragmentManager().beginTransaction();
        tr.setCustomAnimations(animId, 0);
        tr.replace(contId, fg);
        tr.commit();
    }

    public static void replaceWithBackStackWithTransition(int contId,
                                                          int animId, Fragment fg, Fragment fragmentAct) {
        FragmentTransaction tr = fragmentAct
                .getChildFragmentManager().beginTransaction();
        tr.setCustomAnimations(animId, 0);
        tr.replace(contId, fg);
        tr.addToBackStack(null);
        tr.commit();
    }

    public static void replaceWithBackStack(int contId, Fragment fg, Fragment fragmentAct) {
        FragmentTransaction tr = fragmentAct
                .getChildFragmentManager().beginTransaction();
        tr.replace(contId, fg);
        tr.addToBackStack(null);
        tr.commit();
    }

    public static Fragment getTopFragment(Fragment fragmentAct, int contId) {
        return fragmentAct.getChildFragmentManager().findFragmentById(contId);
    }

    public static void goBack(Fragment fragmentAct) {
        KeyBoard.hideKeyboard(fragmentAct.getContext());
        FragmentTransaction transaction = fragmentAct.getChildFragmentManager().beginTransaction();
        transaction.remove(ChildFragmentUtils.getTopFragment(fragmentAct, R.id.layout_container));
        transaction.commit();
    }
}