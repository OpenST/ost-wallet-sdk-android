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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ost.walletsdk.models.entities.OstUser;

import java.util.List;

import ost.com.demoapp.AppProvider;
import ost.com.demoapp.R;
import ost.com.demoapp.entity.User;
import ost.com.demoapp.uicomponents.uiutils.FontFactory;
import ost.com.demoapp.util.CommonUtils;

/**
 * {@link RecyclerView.Adapter} that can display a {@link User} and makes a call to the
 * specified {@link OnUserListInteractionListener}.
 */
public class UserListRecyclerViewAdapter extends RecyclerView.Adapter<UserListRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private final OnUserListInteractionListener mListener;
    private TextDrawable.IBuilder mBuilder;

    private UserListRecyclerViewAdapter(List<User> items, OnUserListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public static UserListRecyclerViewAdapter newInstance(List<User> userList, OnUserListInteractionListener userListPresenter) {
        return new UserListRecyclerViewAdapter(userList, userListPresenter);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_user, parent, false);

        Context context = AppProvider.get().getApplicationContext();
        Typeface font = FontFactory.getInstance(context, FontFactory.FONT.LATO).getBold();

        mBuilder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .textColor(
                        context.getResources().getColor(R.color.color_9b9b9b))
                .useFont(font)
                .endConfig()
                .round();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mUser = mValues.get(position);

        Context context = AppProvider.get().getApplicationContext();
        TextDrawable drawable = mBuilder.build(holder.mUser.getUserName().substring(0, 1).toUpperCase(),
                context.getResources().getColor(R.color.color_f4f4f4));
        holder.mImageView.setImageDrawable(drawable);

        holder.mUserName.setText(holder.mUser.getUserName());

        if (OstUser.CONST_STATUS.CREATED.equalsIgnoreCase(holder.mUser.getStatus())) {
            holder.mStatus.setTextColor(Color.RED);
            holder.mStatus.setText("Wallet Setup Incomplete");
            holder.mSendButton.setVisibility(View.GONE);
            holder.mView.setOnClickListener(null);
        } else {
            holder.mStatus.setTextColor(context.getResources().getColor(R.color.color_34445b));
            holder.mStatus.setText(
                    String.format(
                            "Balance: %s %s", CommonUtils.convertWeiToTokenCurrency(holder.mUser.getBalance()),
                            AppProvider.get().getCurrentEconomy().getTokenSymbol()
                    )
            );
            holder.mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListViewInteraction(holder.mUser);
                    }
                }
            });
            holder.mSendButton.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private final ImageView mImageView;
        private final TextView mUserName;
        private final TextView mStatus;
        private final Button mSendButton;

        public User mUser;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.iv_user_image);
            mUserName = (TextView) view.findViewById(R.id.tv_user_name);
            mStatus = (TextView) view.findViewById(R.id.tv_status);
            mSendButton = (Button) view.findViewById(R.id.btn_send_token);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }

    public interface OnUserListInteractionListener {
        void onListViewInteraction(User user);
    }
}