package com.github.tavyy.javasync.synchronization;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
public class Volatile {

    private static final Set<Integer> readValues = new ConcurrentSkipListSet<>();

    static class SharedObject {
        volatile int value;

        void changeValue(){
            value = 2;
        }
    }

    static class Writer extends Thread {

        private final int index;
        private final Volatile.SharedObject sharedObject;

        Writer(int index, Volatile.SharedObject sharedObject) {
            this.index = index;
            this.sharedObject = sharedObject;
        }

        @Override
        public void run() {
            sleepMs(10);
            int before = sharedObject.value;
            int after = sharedObject.value = index;
            log.info("{} -> before: {} after: {}", index, before, after);
            readValues.add(before);
        }


        private void sleepMs(long time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final SharedObject sharedObject = new SharedObject();


        Writer[] writers = new Writer[50];
        for (int i = 0; i < writers.length; i++) {
            writers[i] = new Writer(i + 1, sharedObject);
        }

        for (Writer writer : writers) {
            writer.start();
        }

        for (Writer writer : writers) {
            writer.join();
        }

        readValues.add(sharedObject.value);
        log.info("Read values: {} size: {}", readValues, readValues.size());
    }
}
