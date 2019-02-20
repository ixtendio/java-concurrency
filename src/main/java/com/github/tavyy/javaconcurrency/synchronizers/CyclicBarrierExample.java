package com.github.tavyy.javaconcurrency.synchronizers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;

import static com.github.tavyy.javaconcurrency.util.RunnableWithException.runSafe;

@Slf4j
public class CyclicBarrierExample {

    private static final int TASKS_NO = 10;
    private static final CyclicBarrier barrier = new CyclicBarrier(TASKS_NO, () -> log.info("All waiting threads arrived to barrier."));

    public static void main(String[] args) throws InterruptedException {

        Thread[] threads = new Thread[TASKS_NO];
        for (int i = 0; i < TASKS_NO; i++) {
            final int taskIndex = i + 1;
            threads[i] = new Thread(runSafe(() -> {
                log.info("Before executing subtask {}", taskIndex);
                barrier.await();
                log.info("Executing subtask {}", taskIndex);
            }));
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

    }

}
