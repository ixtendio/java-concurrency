package com.github.tavyy.javaconcurrency.collections;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tavyy.javaconcurrency.util.RunnableWithException.runSafe;

@Slf4j
public class PoisonPills {

    private static final String POISON_PILL = "POISON_PILL";
    private static final int QUEUE_SIZE = 10;
    private static final BlockingQueue<String> tokens = new LinkedBlockingQueue<>(QUEUE_SIZE);

    public static void main(String[] args) throws Exception {
        AtomicInteger producerCounter = new AtomicInteger(50);

        List<CompletableFuture> tasks = new ArrayList<>();

        //5 Producers
        for (int i = 0; i < 5; i++) {
            tasks.add(CompletableFuture.runAsync(runSafe(() -> {
                while (producerCounter.get() > 0) {
                    Thread.sleep(producerCounter.get());
                    String token = "token-" + producerCounter.getAndDecrement();
                    tokens.put(token);
                    log.info("Added token: {}. Queue size is: {}", token, tokens.size());
                }
                tokens.put(POISON_PILL);
                log.info("PRODUCER STOPPED");
            })));
        }

        //5 Consumers
        for (int i = 0; i < 5; i++) {
            tasks.add(CompletableFuture.runAsync(runSafe(() -> {
                while (true) {
                    Thread.sleep(50);
                    String token = tokens.take();
                    if (POISON_PILL.equals(token)) {
                        break;
                    }
                    log.info("Retrieved token: {}. Tokens queue size: {}", token, tokens.size());
                }
                log.info("CONSUMER  STOPPED");
            })));
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();

    }
}
