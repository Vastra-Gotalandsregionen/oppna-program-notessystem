package se.vgregion.portal.calendar.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Patrik Bergström
 */
public class ThreadPoolFactory {

    public static ExecutorService newCachedThreadPool(int maximumPoolSize) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ((ThreadPoolExecutor) executorService).setMaximumPoolSize(maximumPoolSize);

        return executorService;
    }
}
