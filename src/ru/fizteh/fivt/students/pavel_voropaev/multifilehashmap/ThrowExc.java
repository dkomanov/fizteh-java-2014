package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap;

public final class ThrowExc {
    public static void notEnoughArg(String command, String usage)
            throws IllegalArgumentException {
        throw new IllegalArgumentException(command + ": not enough arguments. " + usage);
    }

    public static void tooManyArg(String command, String usage)
            throws IllegalArgumentException {
        throw new IllegalArgumentException(command + ": too many arguments. " + usage);
    }

    public static void cannotRead(String dbFilePath, String message)
            throws IllegalStateException {
        throw new IllegalStateException(dbFilePath + ": cannot read from file. " + message);
    }

    public static void cannotWrite(String dbFilePath, String message)
            throws IllegalStateException {
        throw new IllegalStateException(dbFilePath + ": cannot write to file. " + message);
    }

    public static void noTable() {
        throw new IllegalArgumentException("no table");
    }
    
    public static void containsWrongFiles(String directory) {
        throw new IllegalArgumentException(directory + " contains wrong files.");
    }
    
}
