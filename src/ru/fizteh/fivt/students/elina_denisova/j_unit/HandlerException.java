package ru.fizteh.fivt.students.elina_denisova.j_unit;

public class HandlerException {
    public static void handler(String message, Throwable cause) {
        System.err.println(message + cause.getMessage());
        System.exit(1);
    }
    public static void handler(Throwable cause) {
        System.err.println(cause.getMessage());
        System.exit(1);
    }

}


