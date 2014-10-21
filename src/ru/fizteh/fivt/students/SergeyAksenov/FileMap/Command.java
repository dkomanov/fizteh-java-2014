package ru.fizteh.fivt.students.SergeyAksenov.FileMap;


public interface Command {
    void run(final String[] args, FileDataBase dataBase, Environment env)
            throws FileMapException, FileMapExitException;
}
