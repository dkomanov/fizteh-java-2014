package ru.fizteh.fivt.students.hromov_igor.multifilemap.exception;

public class ErrorHandler {
    public static String ArgNumHandler() {
        return ("wrong number of arguments");
    }

    public static String NullTableException() {
        return ("null table error");
    }

    public static String PathNameException() {
        return ("path processing failed");
    }

    public static String RootDirException() {
        return ("root directory contains non-directory files");
    }

    public static String UnsavedChangedException() {
        return ("unsaved changes");
    }

    public static String AlreadyUsingException() {
        return ("is already using");
    }

}
