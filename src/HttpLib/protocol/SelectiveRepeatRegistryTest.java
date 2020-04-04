package HttpLib.protocol;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SelectiveRepeatRegistryTest {

    @org.junit.jupiter.api.Test
    void threadSafe() throws InterruptedException {
        // Not robust, simply a general check to ensure that nothing blocks within a given time...
        SelectiveRepeatRegistry sr = new SelectiveRepeatRegistry(10);

        // Test threads
        Runnable testSRR = () -> {
            // each thread will try to send 4 packet .. kinda
            int count = 0;
            while (count < 4) {

                while (!sr.available()) {
                    System.out.println(Thread.currentThread() + " | Window full. Waiting for simulation...");
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Query for next
                int seqNumber = sr.requestNext();
                System.out.println(Thread.currentThread() + " | Got number: " + seqNumber);

                // Simulate RTT
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // .. when we receive ack...
                System.out.println("Confirming: " + seqNumber);
                sr.release(seqNumber);

                count++;
            }
        };

        // Test procedure with concurrency
        int tCount = 11;
        ArrayList<Thread> threads = new ArrayList<>(tCount);
        for (int i = 0; i < tCount; i++) {
            Thread t = new Thread(testSRR);
            t.start();
            threads.add(t);
        }

        // Everything should conclude in time
        for (int i = 0; i < tCount; i++) {
            threads.get(i).join(1000);
            assertFalse(threads.get(i).isAlive());
        }

    }

    @org.junit.jupiter.api.Test
    void availableCases() {
        SelectiveRepeatRegistry sr = new SelectiveRepeatRegistry(4);

        // Basic
        assertTrue(sr.available());
    }

    @org.junit.jupiter.api.Test
    void requestNextSeqNumber() {
        SelectiveRepeatRegistry sr = new SelectiveRepeatRegistry(2);

        assertTrue(sr.requestNext() == 0);
        assertTrue(sr.requestNext() == 1);
        assertTrue(sr.requestNext() == -1); // Window is full, should return invalid (-1) number.
    }

    @org.junit.jupiter.api.Test
    void sync() {
        SelectiveRepeatRegistry sr = new SelectiveRepeatRegistry();
        sr.sync(45812);

        assertTrue(sr.getBase() == 45812);
    }

    @org.junit.jupiter.api.Test
    void inWindowBasic() {
        SelectiveRepeatRegistry sr = new SelectiveRepeatRegistry(2);

        assertTrue(sr.inWindow(0));
        assertTrue(sr.inWindow(1));
        assertFalse(sr.inWindow(-1));
        assertFalse(sr.inWindow(2));
        assertFalse(sr.inWindow(7));
    }

    @org.junit.jupiter.api.Test
    void inWindowAfterMove() {
        System.out.println(Integer.MAX_VALUE);
        SelectiveRepeatRegistry sr = new SelectiveRepeatRegistry(4);

        // Move 2
        for (int i = 0; i < 2; i++)
            sr.release(sr.requestNext());

        assertTrue(sr.inWindow(2));
        assertTrue(sr.inWindow(5));
        assertFalse(sr.inWindow(1));
        assertFalse(sr.inWindow(6));
    }

    @org.junit.jupiter.api.Test
    void inWindowCycling() {
        SelectiveRepeatRegistry sr = new SelectiveRepeatRegistry(2);
        sr.sync(Integer.MAX_VALUE);

        assertTrue(sr.inWindow(Integer.MAX_VALUE));
        assertTrue(sr.inWindow(0));
        assertFalse(sr.inWindow(-1));
        assertFalse(sr.inWindow(Integer.MAX_VALUE-1));
        assertFalse(sr.inWindow(1));
        assertFalse(sr.inWindow(2));
    }
}