package com.arki.backuphelper.gui.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadUtil {

    private static ExecutorService executorService;
    private static AtomicLong counter = new AtomicLong();
    public static ConcurrentHashMap<Long, Future> futureMap = new ConcurrentHashMap<>();

    public static synchronized  <T>  long submitTask(Callable<T> task) {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        Future<T> submit = executorService.submit(task);
        long futureId = counter.getAndAdd(1);
        futureMap.put(futureId, submit);
        return futureId;
    }
}
