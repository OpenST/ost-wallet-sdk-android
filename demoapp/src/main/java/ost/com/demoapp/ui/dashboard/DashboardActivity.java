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

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ost.walletsdk.models.entities.OstUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.ui.BaseActivity;
import ost.com.demoapp.ui.dashboard.dummy.DummyContent;
import ost.com.demoapp.ui.workflow.walletsetup.WalletSetUpFragment;
import ost.com.demoapp.util.FragmentUtils;

public class DashboardActivity extends BaseActivity implements
 UserFragment.OnListFragmentInteractionListener,
 WalletSetUpFragment.OnFragmentInteractionListener{

    private ViewPager mViewPager;
    private List<Fragment> mHomeFragmentList;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);

        mHomeFragmentList = new ArrayList<>();
        mHomeFragmentList.add(UserFragment.newInstance());
        mHomeFragmentList.add(UserFragment.newInstance());
        mHomeFragmentList.add(UserFragment.newInstance());

        List<String> title= new ArrayList<>();
        title.add("Users");
        title.add("Wallet");
        title.add("Settings");

        mViewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager(), mHomeFragmentList, title));

        mTabLayout = (TabLayout) findViewById(R.id.home_navigation);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(R.drawable.ic_notifications_black_24dp);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(R.drawable.ic_home_black_24dp);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(R.drawable.ic_home_black_24dp);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setIcon(R.drawable.ic_home_black_24dp);
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setIcon(R.drawable.ic_home_black_24dp);

        OstUser ostUser = AppProvider.get().getCurrentUser().getOstUser();
        if (!(ostUser.isActivated() || ostUser.isActivating())) {
            FragmentUtils.addFragmentWithoutBackStack(R.id.layout_container,
                    WalletSetUpFragment.newInstance(),
                    this);
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void removeTopFragment() {
        FragmentUtils.goBack(this);
    }

    @Override
    public void goBack() {
        if (!FragmentUtils.isBackStackEmpty(this)) {
            FragmentUtils.goBack(this);
        }
    }
}