package com.billeteraVirtual.users.util;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static long calculateDuration(long startTime) {
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public static void waitRandomMiliSeconds(int min, int max) {
        int milisegundos = ThreadLocalRandom.current().nextInt(min, max);
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
