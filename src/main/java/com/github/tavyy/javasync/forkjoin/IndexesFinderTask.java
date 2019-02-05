package com.github.tavyy.javasync.forkjoin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class IndexesFinderTask extends RecursiveTask<Set<Integer>> {

    static final int MAX_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final AtomicInteger tasks = new AtomicInteger(0);
    private final int splitIndex;
    private final int element;
    private List<Integer> list;

    private IndexesFinderTask(List<Integer> list, int element, int splitIndex) {
        this.list = list;
        this.element = element;
        this.splitIndex = splitIndex;
    }

    static IndexesFinderTask create(List<Integer> list, int element) {
        tasks.set(0);
        return new IndexesFinderTask(list, element, 0);
    }

    @Override
    protected Set<Integer> compute() {
        if (list.size() >= 2 && tasks.get() < MAX_THREADS) {
            return ForkJoinTask.invokeAll(split())
                    .stream()
                    .map(ForkJoinTask::join)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toCollection(TreeSet::new));
        } else {
            return getIndexes();
        }
    }

    private Collection<IndexesFinderTask> split() {
        int mid = list.size() / 2;

        IndexesFinderTask leftTask = new IndexesFinderTask(list.subList(0, mid), element, splitIndex);
        IndexesFinderTask rightTask = new IndexesFinderTask(list.subList(mid, list.size()), element, splitIndex + mid);

        tasks.incrementAndGet();
        tasks.incrementAndGet();
        return Arrays.asList(leftTask, rightTask);
    }

    private Set<Integer> getIndexes() {
        Set<Integer> result = new TreeSet<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == element) {
                result.add(i + splitIndex + 1);
            }
        }
        return result;
    }
}
