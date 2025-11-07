package com.billeteraVirtual.transacciones.util;

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

    public static String maskToken(String token) {
        if (token == null || token.length() < 10) return "****";
        return token.substring(0, 5) + "*****" + token.substring(token.length() - 3);
    }

}
