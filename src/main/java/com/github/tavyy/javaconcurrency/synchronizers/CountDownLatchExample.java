package com.github.tavyy.javaconcurrency.synchronizers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CountDownLatchExample {

    private static final int TASKS_NO = 10;
    private static final CountDownLatch latch = new CountDownLatch(TASKS_NO);
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {

        executor.submit(() -> {
            startSubTasks();

            latch.await(); //wait the children tasks to finish the work
            log.info("All children tasks finished their work!");
            return null;
        }).get();

        executor.shutdownNow();
    }

    private static void startSubTasks() {
        for (int i = 0; i < TASKS_NO; i++) {
            final int taskIndex = i + 1;
            executor.submit(() -> {
                log.info("Executing subtask {}", taskIndex);
                latch.countDown();
            });
        }
    }
}
