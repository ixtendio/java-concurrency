package com.github.tavyy.javasync.threadlocal;

import lombok.extern.slf4j.Slf4j;

import static com.github.tavyy.javasync.util.RunnableWithException.runSafe;

@Slf4j
public class ThreadLocalExample {

    private static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>();

    private static void printThreadLocalValue() throws InterruptedException {
        Thread.sleep(100);
        log.info("ThreadLocal value: {}", THREAD_LOCAL.get());
    }

    public static void main(String[] args) throws Exception {
        THREAD_LOCAL.set(1);

        Thread[] threads = new Thread[2];
        threads[0] = new Thread(runSafe(() -> {
            THREAD_LOCAL.set(2);
            for (int i = 0; i < 5; i++) {
                printThreadLocalValue();
            }
        }));

        threads[1] = new Thread(runSafe(() -> {
            for (int i = 0; i < 5; i++) {
                printThreadLocalValue();
            }
        }));

        threads[0].start();
        threads[1].start();

        threads[0].join();
        threads[1].join();
    }

}
