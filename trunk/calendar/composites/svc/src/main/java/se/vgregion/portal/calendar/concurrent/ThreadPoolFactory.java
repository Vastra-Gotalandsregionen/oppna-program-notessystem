package se.vgregion.portal.calendar.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Factory class for creating thread pools.
 *
 * @author Patrik Bergström
 */
public final class ThreadPoolFactory {

    private ThreadPoolFactory() {}

    /**
     * Creates an {@link ExecutorService} like {@link java.util.concurrent.Executors#newCachedThreadPool()} with the
     * addition of specifying a maximum pool size.
     *
     * @param maximumPoolSize maximum pool size
     * @return the {@link ExecutorService}
     */
    public static ExecutorService newCachedThreadPool(int maximumPoolSize) {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executorService.setMaximumPoolSize(maximumPoolSize);
        executorService.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executorService;
    }
}
