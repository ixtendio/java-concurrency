package com.github.tavyy.javaconcurrency.forkjoin;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Slf4j
public class ForkJoinIndexesFinder {

    public static void main(String[] args) throws Exception {
        int element = 20;
        Path filePath = Path.of(ForkJoinIndexesFinder.class.getResource("/numbers.txt").toURI());
        List<Integer> list = Files.readAllLines(filePath).stream().map(Integer::parseInt).collect(Collectors.toList());

        long startTime = System.currentTimeMillis();
        sequentiallyExecution(list, element);
        long endTime = System.currentTimeMillis();
        log.info("Sequentially execution time: {} ms", (endTime - startTime));

        startTime = System.currentTimeMillis();
        Set<Integer> result = parallelExecution(list, element);
        endTime = System.currentTimeMillis();
        log.info("Parallel execution time: {} ms", (endTime - startTime));

        log.info("The element {} was found {} times at the following locations {}", element, result.size(), result);
    }

    private static Set<Integer> parallelExecution(List<Integer> list, int element) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(IndexesFinderTask.MAX_THREADS);
        return forkJoinPool.invoke(IndexesFinderTask.create(list, element));
    }

    private static Set<Integer> sequentiallyExecution(List<Integer> list, int element) {
        Set<Integer> result = new TreeSet<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == element) {
                result.add(i + 1);
            }
        }
        return result;
    }
}
