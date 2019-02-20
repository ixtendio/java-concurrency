package com.github.tavyy.javaconcurrency.atomicref;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class AtomicCountersExample {

    private static final AtomicInteger ai = new AtomicInteger(0);
    private static final AtomicLong al = new AtomicLong(10);
    private static final DoubleAccumulator dAcc = new DoubleAccumulator(((left, right) -> left - right), 100);
    private static final DoubleAdder dAdd = new DoubleAdder();
    private static final LongAccumulator lAcc = new LongAccumulator(((left, right) -> left + right), 0);
    private static final LongAdder lAdd = new LongAdder();

    public static void main(String[] args) throws Exception {

        log.info("AtomicInteger before: {}", ai.get());
        log.info("AtomicLong before: {}", al.get());
        log.info("DoubleAccumulator before: {}", dAcc.get());
        log.info("DoubleAdder before: {}", dAdd.doubleValue());
        log.info("LongAccumulator before: {}", lAcc.get());
        log.info("LongAdder before: {}", lAdd.longValue());

        CompletableFuture[] tasks = new CompletableFuture[10];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = CompletableFuture.runAsync(() -> {
                ai.incrementAndGet();
                al.decrementAndGet();
                dAcc.accumulate(10);
                dAdd.add(5);
                lAcc.accumulate(10);
                lAdd.add(-5);
            });
        }

        CompletableFuture.allOf(tasks).get();

        log.info("----------------------------------");
        log.info("AtomicInteger after: {}", ai.get());
        log.info("AtomicLong after: {}", al.get());
        log.info("DoubleAccumulator after: {}", dAcc.get());
        log.info("DoubleAdder after: {}", dAdd.doubleValue());
        log.info("LongAccumulator after: {}", lAcc.get());
        log.info("LongAdder after: {}", lAdd.longValue());

    }

}
