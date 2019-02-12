package com.github.tavyy.javasync.util;

@FunctionalInterface
public interface RunnableWithException {

    void run() throws Exception;

    static java.lang.Runnable runSafe(RunnableWithException runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
