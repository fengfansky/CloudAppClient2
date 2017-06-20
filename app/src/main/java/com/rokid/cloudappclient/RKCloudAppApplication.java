package com.rokid.cloudappclient;

import android.app.Application;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class is a global application,
 * can be used to sets some global variables, methods, thread pool and cache.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class RKCloudAppApplication extends Application {

    private static final int POOL_CORE_SIZE = 1;
    private static final int POOL_MAX_SIZE = 10;
    private static final long POOL_KEEP_TIME = 30L;
    private static final int BLOCKING_QUEUE_CAPACITY = 30;

    private static RKCloudAppApplication instance;
    private ThreadPoolExecutor threadPool;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize the thread pool.
        initThreadPoolExecute();

 /*       // Initialize the class for HTTPS request.
        HttpRequest.initialization(HttpsCertUtils.getSSLParams(null, null, null));
        // When the project is debug mode, open the HTTP request's debug log.
        if (BuildConfig.DEBUG) {
            HttpRequest.debug();
        }*/
    }

    public static RKCloudAppApplication getInstance() {
        return instance;
    }

    /**
     * Executes the given command at some time in the future. The command
     * may execute in a pooled thread, at the discretion of the implementation.
     *
     * @param command the runnable task
     */
    public void threadPoolExecute(Runnable command) {
        threadPool.execute(command);
    }

    /**
     * Creates a new ThreadPoolExecutor with the given initial parameters
     * and default thread factory.
     */
    private void initThreadPoolExecute() {
        threadPool = new ThreadPoolExecutor(POOL_CORE_SIZE, POOL_MAX_SIZE, POOL_KEEP_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY));
    }

}
