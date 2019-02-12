package com.github.tavyy.javasync.synchronizers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Exchanger;

import static com.github.tavyy.javasync.util.RunnableWithException.runSafe;

@Slf4j
public class ExchangerExample {

    private static class MessageBuffer {
        private static final int MAX_CAPACITY = 10;
        private final StringBuilder buffer = new StringBuilder();
        private int counter = 0;

        boolean add(String message) {
            boolean added = false;
            if (counter < MAX_CAPACITY) {
                if (counter > 0) {
                    buffer.append(";");
                }
                buffer.append(message);
                added = true;
                counter++;
            }
            return added;
        }

        void clear() {
            counter = 0;
            buffer.delete(0, buffer.length());
        }

        boolean isEmpty() {
            return counter == 0;
        }

        @Override
        public String toString() {
            return buffer.toString();
        }
    }

    public static void main(String[] args) throws Exception {
        Exchanger<MessageBuffer> exchanger = new Exchanger<>();

        CompletableFuture[] tasks = new CompletableFuture[2];
        tasks[0] = CompletableFuture.runAsync(runSafe(() -> {
            MessageBuffer buffer = new MessageBuffer();
            int i = 0;
            while (i < 100) {
                String message = "message-" + (i + 1);
                if (!buffer.add(message)) {
                    buffer = exchanger.exchange(buffer);
                } else {
                    i++;
                }
            }
            exchanger.exchange(buffer);
            exchanger.exchange(null);
        }));

        tasks[1] = CompletableFuture.runAsync(runSafe(() -> {
            MessageBuffer buffer = new MessageBuffer();
            while (buffer != null) {
                if (!buffer.isEmpty()) {
                    log.info(buffer.toString());
                    buffer.clear();
                }
                buffer = exchanger.exchange(buffer);
            }
        }));

        CompletableFuture.allOf(tasks).get();
    }

}
