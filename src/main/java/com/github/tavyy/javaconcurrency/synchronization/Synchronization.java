package com.github.tavyy.javaconcurrency.synchronization;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Synchronization {

    private final Object muttex = new Object();
    private int counter;

    int countTo10() {
        resetCounter();

        List<Thread> counters = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            counters.add(new Thread(() -> counter++));
        }

        counters.forEach(Thread::start);
        counters.forEach(this::waitToFinish);
        return counter;
    }

    int countTo10UsingSleep() {
        resetCounter();

        List<Thread> counters = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            counters.add(new Thread(() -> {
                sleep10Ms();
                counter++;
            }));
        }

        counters.forEach(Thread::start);
        counters.forEach(this::waitToFinish);
        return counter;
    }

    int countTo10UsingSleepAndLocking() {
        resetCounter();

        List<Thread> counters = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            counters.add(new Thread(() -> {
                sleep10Ms();
                synchronized (muttex) {
                    counter++;
                }
            }));
        }

        counters.forEach(Thread::start);
        counters.forEach(this::waitToFinish);
        return counter;
    }

    private void resetCounter() {
        this.counter = 0;
    }

    private void waitToFinish(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleep10Ms() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Synchronization sync = new Synchronization();

        log.info("countTo10 => {}", sync.countTo10());
        log.info("countTo10UsingSleep => {}", sync.countTo10UsingSleep());
        log.info("countTo10UsingSleepAndLocking => {}", sync.countTo10UsingSleepAndLocking());
    }
}
