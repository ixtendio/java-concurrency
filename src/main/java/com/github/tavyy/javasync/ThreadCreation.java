package com.github.tavyy.javasync;

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
        System.out.println(Thread.currentThread().getName() + ": End of main thread");
    }

    private static Thread newThread() {
        return new Thread() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ": Override the run method");
            }

        };
    }

    private static Thread newThreadWithRunnable() {
        return new Thread(() -> System.out.println(Thread.currentThread().getName() + ": With Runnable"));
    }
}
