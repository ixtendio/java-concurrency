package com.github.tavyy.javasync;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
public class CompletableFutureExample {

    private static Supplier<Integer> get10() {
        return () -> {
            try {
                log.info("get10 called");
                Thread.sleep(100);
                return 10;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Supplier<Integer> get20() {
        return () -> {
            try {
                log.info("get20 called");
                Thread.sleep(50);
                return 20;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Function<Integer, Integer> multiplyWith10UsingApply() {
        return value -> {
            try {
                log.info("multiplyWith10UsingApply called");
                Thread.sleep(30);
                return value * 10;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Function<Integer, CompletableFuture<Integer>> multiplyWith10UsingCompose() {
        return value -> {
            try {
                log.info("multiplyWith10UsingCompose called");
                Thread.sleep(30);
                return supplyAsync(() -> value * 10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Supplier<Integer> get30() {
        return () -> {
            try {
                log.info("get30 called");
                Thread.sleep(150);
                return 30;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Supplier<Integer> throwException() {
        return () -> {
            log.info("throwException called");
            throw new RuntimeException();
        };
    }

    public static void main(String[] args) throws Exception {
        CompletableFuture<Integer> combine2 = supplyAsync(get10()).thenCombine(supplyAsync(get20()), (a, b) -> a + b);
        log.info("COMBINE 2: 10 + 20 = {}", combine2.get());

        CompletableFuture<Integer> combine20With30 = supplyAsync(get20()).thenCombine(supplyAsync(get30()), (a, b) -> a + b);
        CompletableFuture<Integer> combine10With20And30 = supplyAsync(get10()).thenCombine(combine20With30, (a, b) -> a + b);
        log.info("COMBINE 3: 10 + 20 + 30 = {}", combine10With20And30.get());


        CompletableFuture<Void> acceptBoth = supplyAsync(get10())
                .thenAcceptBoth(supplyAsync(get20()),
                        (v1, v2) -> log.info("ACCEPT: {}, {}", v1, v2));
        acceptBoth.get();

        CompletableFuture<Integer> apply = supplyAsync(get10()).thenApply(multiplyWith10UsingApply());
        log.info("APPLY: 10 * 10 = {}", apply.get());

        CompletableFuture<Integer> compose = supplyAsync(get10()).thenCompose(multiplyWith10UsingCompose());
        log.info("COMPOSE: 10 * 10 = {}", compose.get());

        CompletableFuture<Integer> withException = supplyAsync(get10())
                .thenCombine(supplyAsync(throwException()), (a, b) -> a + b)
                .exceptionally(e -> -1);
        log.info("EXCEPTIONALLY: {}", withException.get());
    }

}
