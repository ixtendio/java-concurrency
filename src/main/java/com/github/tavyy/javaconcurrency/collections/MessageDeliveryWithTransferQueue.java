package com.github.tavyy.javaconcurrency.collections;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tavyy.javaconcurrency.util.RunnableWithException.runSafe;

@Slf4j
public class MessageDeliveryWithTransferQueue {

    private static final TransferQueue<String> messages = new LinkedTransferQueue<>();

    public static void main(String[] args) throws Exception {
        CompletableFuture[] tasks = new CompletableFuture[11];
        AtomicInteger messageCounter = new AtomicInteger(1);
        AtomicInteger consumerCounter = new AtomicInteger(tasks.length - 1);

        //Consumer
        tasks[0] = CompletableFuture.runAsync(runSafe(() -> {
            while (consumerCounter.get() > 0) {
                Thread.sleep(100);
                log.info("Retrieved message: {}", messages.poll());
                consumerCounter.decrementAndGet();
            }
        }));

        for (int i = 1; i < tasks.length; i++) {
            CompletableFuture task;
            //Producer
            task = CompletableFuture.runAsync(runSafe(() -> {
                Thread.sleep(messageCounter.get());
                String message = "message-" + messageCounter.incrementAndGet();
                messages.transfer(message);
                log.info("Message: {} has been successfully transferred to consumer", message, messages.size());
            }));
            tasks[i] = task;
        }

        CompletableFuture.allOf(tasks).get();

    }
}