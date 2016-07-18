package com.tamic.inject.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LIUYONGKUI726 on 2016-07-15.
 */
public class HandleThreadPool {

    private final static int POOL_SIZE = 4;// 线程池的大小最好设置成为CUP核数的2N
    private final static int MAX_POOL_SIZE = 6;// 设置线程池的最大线程数
    private final static int KEEP_ALIVE_TIME = 4;// 设置线程的存活时间
    private final Executor mExecutor;
    public HandleThreadPool() {

        ThreadFactory factory = new PriorityThreadFactory("thread-pool", android.os.Process.THREAD_PRIORITY_BACKGROUND);

        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
        mExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, factory);
    }

    public void execute(Runnable command){

        mExecutor.execute(command);
    }

    public Executor getExecutor() {
        return mExecutor;
    }
}
