package com.railway.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockResolution {
    private static final Lock TRAIN_LOCK = new ReentrantLock();
    private static final Lock PAYMENT_LOCK = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread agent1 = new Thread(() -> bookWithOrdering("Agent1"));
        Thread agent2 = new Thread(() -> bookWithOrdering("Agent2"));
        agent1.start();
        agent2.start();
        agent1.join();
        agent2.join();
        System.out.println("Both agents completed without deadlock using consistent lock ordering.");
    }

    private static void bookWithOrdering(String name) {
        // Always acquire TRAIN then PAYMENT
        boolean gotTrain = false;
        boolean gotPayment = false;
        try {
            gotTrain = TRAIN_LOCK.tryLock(500, TimeUnit.MILLISECONDS);
            if (!gotTrain) { System.out.println(name + ": retry later (train)"); return; }
            // simulate delay
            sleep(50);
            gotPayment = PAYMENT_LOCK.tryLock(500, TimeUnit.MILLISECONDS);
            if (!gotPayment) { System.out.println(name + ": retry later (payment)"); return; }
            // critical section
            sleep(50);
            System.out.println(name + ": booking done");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (gotPayment) PAYMENT_LOCK.unlock();
            if (gotTrain) TRAIN_LOCK.unlock();
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}




