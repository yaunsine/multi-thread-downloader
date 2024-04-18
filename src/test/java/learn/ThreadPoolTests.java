package learn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTests {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = null;
        try {
            threadPoolExecutor = new ThreadPoolExecutor(1, 2, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5));
            Runnable r = () -> System.out.println(Thread.currentThread().getName());

            for (int i = 0; i < 3; i++) {
                threadPoolExecutor.execute(r);
            }
            System.out.println(threadPoolExecutor);
        } finally {
            if (threadPoolExecutor != null) {
                threadPoolExecutor.shutdown();
            }
            assert threadPoolExecutor != null;
            if (threadPoolExecutor.isTerminated()) {
                threadPoolExecutor.shutdownNow();
            }
        }

    }
}
