package com.github.tavyy.javasync.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadCreation {

    public static void main(String[] args) throws Exception {
        Thread thread1 = newThread();
        Thread thread2 = newThreadWithRunnable();

        //Start the threads
        thread1.start();
        thread2.start();

        //Wait both threads to finish before exit the program
        thread1.join();
        thread2.join();
        log.info("End of main thread");
    }

    private static Thread newThread() {
        return new Thread() {

            @Override
            public void run() {
                log.info("Override the run method");
            }

        };
    }

    private static Thread newThreadWithRunnable() {
        return new Thread(() -> log.info("With Runnable"));
    }
}
