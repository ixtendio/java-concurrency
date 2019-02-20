package com.github.tavyy.javaconcurrency.deadlock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeadlockAvoidance {

    private static final Object muttex1 = new Object();
    private static final Object muttex2 = new Object();

    private static final Thread worker1 = new Thread(() -> {
        synchronized (muttex1) {
            sleep5Ms();
            synchronized (muttex2) {
                log.info("Worker1 finished");
            }
        }
    });

    private static final Thread worker2 = new Thread(() -> {
        synchronized (muttex1) {
            sleep5Ms();
            synchronized (muttex2) {
                log.info("Worker2 finished");
            }
        }
    });

    private static void sleep5Ms() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        worker1.start();
        worker2.start();

        worker1.join();
        worker2.join();
    }

}
