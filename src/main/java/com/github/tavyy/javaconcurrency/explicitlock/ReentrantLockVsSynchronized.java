package com.github.tavyy.javaconcurrency.explicitlock;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.tavyy.javaconcurrency.util.RunnableWithException.runSafe;

@Slf4j
public class ReentrantLockVsSynchronized {

    private final ReentrantLock lock = new ReentrantLock();

    private String generateWithLock() throws InterruptedException {
        boolean locked = false;
        try {
            locked = lock.tryLock();
            if (locked) {
                Thread.sleep(1_000);
                return UUID.randomUUID().toString();
            } else {
                return "BUSY";
            }
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    private synchronized String generateWithSynchronized() throws InterruptedException {
        log.info("Generating a new access token...");
        Thread.sleep(1_000);
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        ReentrantLockVsSynchronized test = new ReentrantLockVsSynchronized();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 10; i++) {
//            executor.submit(runSafe(() -> log.info(test.generateWithLock())));
            executor.submit(runSafe(() -> log.info(test.generateWithSynchronized())));
        }

        executor.shutdown();
    }

}
