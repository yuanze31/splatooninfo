package com.yuanze31.splatooninfo.utils;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceManager {
    
    private static ExecutorServiceManager instance;
    
    private final ExecutorService downloadExecutor;
    private final ExecutorService ioExecutor;
    
    private ExecutorServiceManager(Context context) {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        downloadExecutor = Executors.newFixedThreadPool(Math.max(2, corePoolSize));
        ioExecutor = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized ExecutorServiceManager getInstance(Context context) {
        if (instance == null) {
            instance = new ExecutorServiceManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public ExecutorService getDownloadExecutor() {
        return downloadExecutor;
    }
    
    public ExecutorService getIOExecutor() {
        return ioExecutor;
    }
    
    public void shutdown() {
        if (downloadExecutor != null && !downloadExecutor.isShutdown()) {
            downloadExecutor.shutdown();
        }
        if (ioExecutor != null && !ioExecutor.isShutdown()) {
            ioExecutor.shutdown();
        }
    }
}
