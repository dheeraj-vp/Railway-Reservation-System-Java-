package com.railway.service;

public class DeadlockDemo {
    private static final Object TRAIN_LOCK = new Object();
    private static final Object PAYMENT_LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread agent1 = new Thread(() -> {
            synchronized (TRAIN_LOCK) {
                sleep(100);
                synchronized (PAYMENT_LOCK) {
                    System.out.println("Agent1 completed");
                }
            }
        }, "Agent1");

        Thread agent2 = new Thread(() -> {
            synchronized (PAYMENT_LOCK) {
                sleep(100);
                synchronized (TRAIN_LOCK) {
                    System.out.println("Agent2 completed");
                }
            }
        }, "Agent2");

        agent1.start();
        agent2.start();

        // Wait a bit and report deadlock likelihood
        Thread.sleep(1000);
        System.out.println("If the program hangs here, a deadlock occurred (expected for demo).");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}




