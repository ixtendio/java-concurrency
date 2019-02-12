package com.github.tavyy.javasync.synchronization;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tavyy.javasync.util.RunnableWithException.runSafe;

@Slf4j
public class ProducerConsumer {

    private static final int MAX_NO_TOKENS = 10;
    private final Queue<String> tokens = new LinkedList<>();

    private void produce(String token) throws InterruptedException {
        synchronized (tokens) {
            while (tokens.size() == MAX_NO_TOKENS) {
                log.info("Tokens queue is full. Waiting for consumers...");
                tokens.wait();
            }
            tokens.offer(token);
            log.info("Added token: {}. Queue size is: {}", token, tokens.size());
            tokens.notifyAll();
        }
    }

    private String consume() throws InterruptedException {
        synchronized (tokens) {
            String token;
            while ((token = tokens.poll()) == null) {
                log.info("Tokens queue is empty. Waiting for producers...");
                tokens.wait();
            }
            tokens.notifyAll();
            return token;
        }
    }

    public static void main(String[] args) throws Exception {
        AtomicInteger producerCounter = new AtomicInteger(50);
        AtomicInteger consumerCounter = new AtomicInteger(50);
        ProducerConsumer pc = new ProducerConsumer();

        CompletableFuture[] tasks = new CompletableFuture[10];
        for (int i = 0; i < tasks.length; i++) {
            CompletableFuture task;
            if (i < 5) {
                //Producer
                task = CompletableFuture.runAsync(runSafe(() -> {
                    while (producerCounter.get() > 0) {
                        Thread.sleep(producerCounter.get());
                        pc.produce("token-" + producerCounter.getAndDecrement());
                    }
                }));
            } else {
                //Consumer
                task = CompletableFuture.runAsync(runSafe(() -> {
                    while (consumerCounter.get() > 0) {
                        Thread.sleep(consumerCounter.get() * 2L);
                        log.info("Retrieved token: {}", pc.consume());
                        consumerCounter.decrementAndGet();
                    }
                }));
            }
            tasks[i] = task;
        }

        CompletableFuture.allOf(tasks).get();

    }


}
