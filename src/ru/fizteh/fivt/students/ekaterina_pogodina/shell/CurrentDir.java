package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

public final class CurrentDir {
    private static String currentDirectory = System.getProperty("user.home");

    private CurrentDir() {
    }

    public static String getCurrentDirectory() {
        return currentDirectory;
    }

    public static void changeCurrentDirectory(String newDirectory) {
        currentDirectory = newDirectory;
    }

}
