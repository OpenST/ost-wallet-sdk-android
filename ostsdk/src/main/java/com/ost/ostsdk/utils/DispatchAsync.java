package com.ost.ostsdk.utils;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class DispatchAsync extends AsyncTask<Void, Void, Void> {
    private WeakReference<Executor> mExecutor;

    public static void dispatch(Executor executor) {
        new DispatchAsync(executor).execute();
    }

    private DispatchAsync(Executor executor) {
        mExecutor = new WeakReference<>(executor);
    }

    @Override
    protected Void doInBackground(final Void... params) {
        if (null != mExecutor.get()) {
            mExecutor.get().execute();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (null != mExecutor.get()) {
            mExecutor.get().onExecuteComplete();
        }
        super.onPostExecute(aVoid);
    }

    public abstract static class Executor {
        public abstract void execute();

        public void onExecuteComplete() {
        }
    }
}