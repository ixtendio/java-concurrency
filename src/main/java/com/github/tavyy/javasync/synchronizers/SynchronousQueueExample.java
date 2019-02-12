package com.github.tavyy.javasync.synchronizers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;

import static com.github.tavyy.javasync.util.RunnableWithException.runSafe;

@Slf4j
public class SynchronousQueueExample {

    private static final BlockingQueue<String> queue = new SynchronousQueue<>();

    public static void main(String[] args) throws Exception {

        CompletableFuture[] tasks = new CompletableFuture[2];

        tasks[0] = CompletableFuture.runAsync(runSafe(() -> {
            for (int i = 0; i < 10; i++) {
                String message = "message-" + i;
                queue.put(message);
                log.info("Put message: {}", message);
            }
        }));

        tasks[1] = CompletableFuture.runAsync(runSafe(() -> {
            for (int i = 0; i < 10; i++) {
                log.info("Take message: {}", queue.take());
            }
        }));

        CompletableFuture.allOf(tasks).get();
    }
}
