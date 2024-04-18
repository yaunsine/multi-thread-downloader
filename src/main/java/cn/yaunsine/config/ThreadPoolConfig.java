package cn.yaunsine.config;

import sun.management.ThreadInfoCompositeData;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolConfig {
    private static int corePoolSize = 2;
    private static int maxPoolSize = 3;
    private static int keepAliveTime = 1;
    private static int queueTaskCapacity = 2;

    private static ThreadPoolExecutor threadPoolExecutor = null;

    public static ThreadPoolExecutor newThreadPoolExecutor() {
        /**
         * ThreadPoolExecutor(int corePoolSize,
         *                               int maximumPoolSize,
         *                               long keepAliveTime,
         *                               TimeUnit unit,
         *                               BlockingQueue<Runnable> workQueue)
         */

        synchronized (ThreadPoolExecutor.class) {
            if (threadPoolExecutor == null) {
                threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                        TimeUnit.MINUTES, new ArrayBlockingQueue<>(queueTaskCapacity)
                );
            }
        }

        return threadPoolExecutor;
    }
    public ThreadFactory getThreadFactory() {
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        };
        return threadFactory;
    }
}
