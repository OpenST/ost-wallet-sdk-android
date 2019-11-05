/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import com.ost.walletsdk.annotations.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ost.ostwallet.AppProvider;
import com.ost.ostwallet.R;
import com.ost.ostwallet.entity.User;
import com.ost.ostwallet.ui.BaseFragment;
import com.ost.ostwallet.uicomponents.AppBar;
import com.ost.ostwallet.util.WrapLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserListFragment extends BaseFragment implements UserListView,
        UserListRecyclerViewAdapter.OnUserListInteractionListener {

    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mPullToRefresh;

    private UserListPresenter mUserListPresenter = UserListPresenter.newInstance();
    private UserListRecyclerViewAdapter mUserListRecyclerViewAdapter;
    private List<User> mUserList;
    private Boolean paginationRequestSent = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserListFragment() {
    }


    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_user_list, container, false);

        ((TextView)view.findViewById(R.id.tv_heading)).setText(
                String.format(
                        "Send %s tokens to users from\n the economy.",
                        AppProvider.get().getCurrentEconomy().getTokenSymbol()
                )
        );
        (view.findViewById(R.id.tv_heading)).setVisibility(View.GONE);

        Context context = view.getContext();

        AppBar appBar = AppBar.newInstance(getContext(),
                "Users",
                false);
        setUpAppBar(view, appBar);

        mRecyclerView = view.findViewById(R.id.rv_users);
        mPullToRefresh = view.findViewById(R.id.pullToRefresh);

        mUserList = new ArrayList<User>();
        mUserListPresenter.setUserList(mUserList);
        mUserListRecyclerViewAdapter = UserListRecyclerViewAdapter.newInstance(mUserList, this);

        LinearLayoutManager layoutManager = new WrapLinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mUserListRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!paginationRequestSent && (visibleItemCount + firstVisibleItemPosition) >=
                        totalItemCount && firstVisibleItemPosition >= 0) {
                    paginationRequestSent = true;
                    mUserListPresenter.updateUserList(false);
                }
            }
        });

        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUserListPresenter.updateUserList(true);
                mPullToRefresh.setRefreshing(false);
            }
        });

        mUserListPresenter.attachView(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListViewInteraction(User user) {
        mListener.onListFragmentInteraction(user);
    }

//    @Override
//    public void goToWalletDetails(){
//        mListener.goToWalletDetails();
//    }

    @Override
    public void notifyDataSetChanged() {
        paginationRequestSent = false;
        mUserListRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void goToWalletDetails();
        void onListFragmentInteraction(User user);
    }
}