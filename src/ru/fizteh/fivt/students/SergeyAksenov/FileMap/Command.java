package ru.fizteh.fivt.students.SergeyAksenov.FileMap;


public interface Command {
    void run(final String[] args, DataBase dataBase, Environment env)
            throws FileMapException, FileMapExitException;
}
