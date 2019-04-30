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

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

class HomePagerAdapter extends FragmentPagerAdapter {
    private final List<String> mTitleList;
    private List<Fragment> mHomeFragmentList;

    public HomePagerAdapter(FragmentManager fragmentManager, List<Fragment> homeFragmentList, List<String> titleList) {
        super(fragmentManager);
        mHomeFragmentList = homeFragmentList;
        mTitleList = titleList;
    }

    @Override
    public Fragment getItem(int i) {
        return mHomeFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mHomeFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}