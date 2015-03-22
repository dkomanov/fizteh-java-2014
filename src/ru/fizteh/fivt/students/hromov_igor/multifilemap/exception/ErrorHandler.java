package ru.fizteh.fivt.students.hromov_igor.multifilemap.exception;

public class ErrorHandler {
    public static String argNumHandler() {
        return ("wrong number of arguments");
    }

    public static String nullTableException() {
        return ("null table error");
    }

    public static String pathNameException() {
        return ("path processing failed");
    }

    public static String rootDirException() {
        return ("root directory contains non-directory files");
    }

    public static String unsavedChangedException() {
        return ("unsaved changes");
    }

    public static String alreadyUsingException() {
        return ("is already using");
    }

}
