package ru.fizteh.fivt.students.standy66_new.runners;

/**
 * Created by andrew on 21.11.14.
 */
public final class Task4Runner {
    private Task4Runner() {
    }

    public static void main(String[] args) {
        System.setProperty("warn_unsaved", "true");
        Task3Runner.main(args);
    }
}
