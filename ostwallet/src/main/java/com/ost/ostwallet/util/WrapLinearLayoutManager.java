package com.ost.ostwallet.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class WrapLinearLayoutManager extends LinearLayoutManager {
    private static final String LOG_TAG = "WrapLinearLayoutManager";

    public WrapLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e(LOG_TAG, "IndexOutOfBoundsException");
        }
    }
}
