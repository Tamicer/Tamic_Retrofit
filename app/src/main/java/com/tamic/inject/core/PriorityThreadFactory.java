package com.tamic.inject.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import android.os.Process;

/**
 * Created by Tamic on 2016-07-15.
 */
public class PriorityThreadFactory implements ThreadFactory {

    private final String mName;
    private final int mPriority;
    private final AtomicInteger mNumber = new AtomicInteger();

    public PriorityThreadFactory(String name, int priority) {
        mName = name;
        mPriority = priority;
    }
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, mName +"-"+mNumber.getAndIncrement()){
            @Override
            public void run() {
                Process.setThreadPriority(mPriority);
                super.run();
            }
        };
    }
}
