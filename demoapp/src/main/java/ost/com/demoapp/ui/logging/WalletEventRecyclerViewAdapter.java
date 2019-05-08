/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.logging;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import ost.com.demoapp.R;
import ost.com.demoapp.entity.OstLogEvent;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OstLogEvent} and makes a call to the

 */
public class WalletEventRecyclerViewAdapter extends RecyclerView.Adapter<WalletEventRecyclerViewAdapter.ViewHolder> {

    private final List<OstLogEvent> mValues;

    public WalletEventRecyclerViewAdapter(List<OstLogEvent> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTime.setText(new Date(holder.mItem.getId()).toString());
        holder.mWorkflowId.setText(String.valueOf(holder.mItem.getWorkflowId()));
        holder.mWorkflowType.setText(holder.mItem.getWorkflow());
        holder.mWorkflowCallback.setText(holder.mItem.getCallbackName());
        holder.mContentView.setText(mValues.get(position).getDetails());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTime;
        public final TextView mContentView;
        private final TextView mWorkflowId;
        private final TextView mWorkflowCallback;
        private final TextView mWorkflowType;
        public OstLogEvent mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTime = (TextView) view.findViewById(R.id.tv_timestamp);
            mWorkflowId = (TextView) view.findViewById(R.id.tv_workflow_id);
            mWorkflowType = (TextView) view.findViewById(R.id.tv_workflow_type);
            mWorkflowCallback = (TextView) view.findViewById(R.id.tv_workflow_callback);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}