package com.coditory.mongo.shared;

public class Logger {
    public static void stdout(String message, Object... args) {
        System.out.println(String.format(message, args));
    }

    public static void stderr(String message, Object... args) {
        System.out.println(String.format(message, args));
    }
}
