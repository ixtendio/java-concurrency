package com.github.tavyy.javaconcurrency.threadlocal;

import lombok.extern.slf4j.Slf4j;

import static com.github.tavyy.javaconcurrency.util.RunnableWithException.runSafe;

@Slf4j
public class InheritableThreadLocalExample {

    private static final ThreadLocal<Integer> THREAD_LOCAL = new InheritableThreadLocal<>();

    private static void printThreadLocalValue() throws InterruptedException {
        Thread.sleep(100);
        log.info("ThreadLocal value: {}", THREAD_LOCAL.get());
    }

    private static Thread spawnChildThread() {
        return new Thread(runSafe(() -> {
            for (int i = 0; i < 5; i++) {
                printThreadLocalValue();
            }
        }));
    }

    public static void main(String[] args) throws Exception {
        THREAD_LOCAL.set(1);

        Thread thread = new Thread(runSafe(() -> {
            Thread childThread = spawnChildThread();
            childThread.start();
            for (int i = 0; i < 5; i++) {
                printThreadLocalValue();
            }
            childThread.join();
        }));

        thread.start();
        thread.join();
    }
}
