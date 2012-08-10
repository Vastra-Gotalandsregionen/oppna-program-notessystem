package se.vgregion.portal.calendar.concurrent;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Patrik Bergström
 */
public class ThreadPoolFactoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolFactoryTest.class);

    @Test
    public void testNewCachedThreadPool() throws Exception {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) ThreadPoolFactory.newCachedThreadPool(10);

        final String mainThreadName = Thread.currentThread().getName();
        final AtomicBoolean mainThreadHasBeenUsedByAtLeastOneTask = new AtomicBoolean(false);

        final AtomicInteger integer = new AtomicInteger(0); // Provides thread-safe increments

        // We test that the tasks aren't rejected even though we submit substantially more than the maximum pool size
        // (The tasks that can't be run in the thread pool's threads should be run in the main thread due to the
        // RejectedExecutionHandler set by the factory).
        for (int i = 0; i < 50; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        String threadName = Thread.currentThread().getName();

                        if (threadName.equals(mainThreadName)) {
                            mainThreadHasBeenUsedByAtLeastOneTask.set(true);
                        }
                        Thread.sleep(100);
                        integer.addAndGet(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            });
        }

        int poolSize = executorService.getPoolSize();
        assertEquals(10, poolSize);

        // Initiate an orderly shutdown and wait for it to complete, waiting no more than necessary.
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);

        assertEquals(50, integer.get());
        assertTrue(mainThreadHasBeenUsedByAtLeastOneTask.get());
    }

}
