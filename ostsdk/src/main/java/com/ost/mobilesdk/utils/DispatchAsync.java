package com.ost.mobilesdk.utils;

import com.ost.mobilesdk.OstConstants;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class DispatchAsync {
    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(OstConstants.THREAD_POOL_SIZE);

    public static Future<AsyncStatus> dispatch(Executor executor) {
        return THREAD_POOL_EXECUTOR.submit(executor);
    }

    public abstract static class Executor implements Callable<AsyncStatus> {
    }
}