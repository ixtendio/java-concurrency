package com.github.tavyy.javasync.threadlocal;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.tavyy.javasync.util.RunnableWithException.runSafe;

@Slf4j
public class InheritableThreadLocalExampleUsingThreadPool {

    private static final ThreadLocal<Integer> threadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) throws Exception {

        CountDownLatch cdl = new CountDownLatch(10);
        threadLocal.set(1);
        log.info("The thread local value on the main thread is 1");
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 10; i++) {
            executor.submit(runSafe(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        Thread.sleep(100);
                        log.info("ThreadLocal value: {}", threadLocal.get());
                    }
                } finally {
                    cdl.countDown();
                }
            }));
        }

        Thread.sleep(100);
        threadLocal.set(2);
        log.info("The thread local value on the main thread is 2");
        cdl.await();
        executor.shutdown();
    }
}
