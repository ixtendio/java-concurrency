package com.github.tavyy.javasync.atomicref;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.tavyy.javasync.util.RunnableWithException.runSafe;

@Slf4j
public class ConcurrentStack<T> {

    @AllArgsConstructor
    private static class Node<T> {
        private final T value;
        private final Node<T> next;
    }

    private final AtomicReference<Node<T>> headRef = new AtomicReference<>();

    public void push(T element) {
        Node<T> newHead;
        Node<T> oldHead;
        do {
            oldHead = headRef.get();
            newHead = new Node<>(element, oldHead);
        } while (!headRef.compareAndSet(oldHead, newHead));
    }

    public Optional<T> pop() {
        Node<T> newHead;
        Node<T> head;
        do {
            head = headRef.get();
            if (head == null) {
                return Optional.empty();
            }
            newHead = head.next;
        } while (!headRef.compareAndSet(head, newHead));
        return Optional.of(head.value);
    }

    public List<T> traverse() {
        List<T> elements = new ArrayList<>();
        Node<T> head = headRef.get();
        while (head != null) {
            elements.add(head.value);
            head = head.next;
        }
        return elements;
    }

    public static void main(String[] args) throws Exception {
        AtomicInteger counter = new AtomicInteger();
        ConcurrentStack<Integer> stack = new ConcurrentStack<>();

        List<CompletableFuture> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(CompletableFuture.runAsync(runSafe(() -> {
                Thread.sleep(10);
                stack.push(counter.getAndIncrement());
            })));
            tasks.add(CompletableFuture.runAsync(runSafe(() -> {
                Thread.sleep(11);
                log.info("Pop: {}", stack.pop().orElse(null));
            })));
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();

        List<Integer> stackElements = stack.traverse();
        log.info("Stack size: {}", stackElements.size());
        log.info("Stack elements: {}", stackElements);
    }
}
