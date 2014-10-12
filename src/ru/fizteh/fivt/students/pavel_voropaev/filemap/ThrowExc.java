package ru.fizteh.fivt.students.pavel_voropaev.filemap;

public final class ThrowExc {
    public static void notEnoughArg(String command, String usage)
            throws IllegalArgumentException {
        throw new IllegalArgumentException(command + ": Not enough arguments. " + usage);
    }

    public static void tooManyArg(String command, String usage)
            throws IllegalArgumentException {
        throw new IllegalArgumentException(command + ": Too many arguments." + usage);
    }

    public static void cannotRead(String dbFilePath, String message)
            throws IllegalStateException {
        throw new IllegalStateException(dbFilePath + ": cannot read from file, " + message);
    }

    public static void cannotWrite(String dbFilePath, String message)
            throws IllegalStateException {
        throw new IllegalStateException(dbFilePath + ": cannot write to file, " + message);
    }
    
}
