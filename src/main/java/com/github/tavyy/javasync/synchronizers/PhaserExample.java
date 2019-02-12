package com.github.tavyy.javasync.synchronizers;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

@Slf4j
public class PhaserExample {

    static class Task implements Runnable {

        private final int index;

        Task(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            log.info("Executing task {}", index);
        }
    }

    private static void executeTasks(final List<Task> tasks, final int count) {
        Phaser phaser = new Phaser(1) {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                return phase >= count - 1 || registeredParties == 0;
            }
        };

        for (Task task : tasks) {
            phaser.register();
            new Thread(() -> {
                do {
                    log.info("Before executing task {}", task.index);
                    phaser.arriveAndAwaitAdvance();
                    task.run();
                } while (!phaser.isTerminated());
            }).start();
        }

        phaser.arriveAndDeregister();
    }

    public static void main(String[] args) {
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            tasks.add(new Task(i));
        }

        //execute the tasks twice
        executeTasks(tasks, 2);
    }
}
