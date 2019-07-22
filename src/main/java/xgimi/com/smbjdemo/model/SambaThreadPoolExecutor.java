package xgimi.com.smbjdemo.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:anlong.jiang
 * Time:13:53
 * Description:This is SambaThreadPoolExecutor
 **/
public class SambaThreadPoolExecutor {
    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private final int KEEP_ALIVE = 1;
    private final ThreadFactory factory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Samba #" + mCount.getAndIncrement());
        }
    };
    private final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<>(64);
    private final Executor mThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, factory);
    private static SambaThreadPoolExecutor mExecutor;

    public static synchronized SambaThreadPoolExecutor getInstance() {
        if (mExecutor == null) {
            mExecutor = new SambaThreadPoolExecutor();
        }
        return mExecutor;
    }

    public void execute(Runnable runnable) {
        mThreadPool.execute(runnable);
    }
}
