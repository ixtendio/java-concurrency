package com.github.tavyy.javaconcurrency.synchronizers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

import static com.github.tavyy.javaconcurrency.util.RunnableWithException.runSafe;

@Slf4j
public class SemaphoreExample {

    private static final int TASKS_NO = 10;
    private static final Semaphore semaphore = new Semaphore(2);

    public static void main(String[] args) throws InterruptedException {

        Thread[] threads = new Thread[TASKS_NO];
        for (int i = 0; i < TASKS_NO; i++) {
            final int taskIndex = i + 1;
            threads[i] = new Thread(runSafe(() -> {
                log.info("Before executing subtask {}", taskIndex);
                semaphore.acquire();
                log.info("Executing subtask {}", taskIndex);
            }));
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

    }
}
