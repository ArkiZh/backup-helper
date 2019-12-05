package com.arki.backuphelper.gui.util;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class ThreadUtil {

    public enum ThreadType {
        SCAN
    }

    static{
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Long, Thread> entry : threadMap.entrySet()) {
                    if (!entry.getValue().isAlive()) {
                        threadMap.remove(entry.getKey());
                        // TODO How to clean future map?
                        // Future future = futureMap.get(entry.getKey());
                    }
                }
            }
        }, "DaemonThreadCleanThreadMap");
        thread.start();
    }

    private static ConcurrentHashMap<ThreadType, Long> threadTypeToIdMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Thread> threadMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Future> futureMap = new ConcurrentHashMap<>();

    public static synchronized <T> long submitTask(Callable<T> callable) {
        FutureTask<T> futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask);
        long threadId = thread.getId();
        threadMap.put(threadId, thread);
        futureMap.put(threadId, futureTask);
        thread.start();
        return threadId;
    }

    public static <T> void submitTask(Callable<T> callable, ThreadType threadType) {
        long threadId = submitTask(callable);
        threadTypeToIdMap.put(threadType, threadId);
    }

    public static Future getFutureTask(long threadId) {
        return futureMap.get(threadId);
    }

    public static Future getFutureTask(ThreadType threadType) {
        return getFutureTask(threadTypeToIdMap.get(threadType));
    }

    public static Thread getThread(long threadId) {
        return threadMap.get(threadId);
    }

    public static Thread getThread(ThreadType threadType) {
        return getThread(threadTypeToIdMap.get(threadType));
    }

    public static void interruptTask(ThreadType threadType) {
        Thread thread = threadMap.get(threadTypeToIdMap.get(threadTypeToIdMap));
        thread.interrupt();
    }
}
