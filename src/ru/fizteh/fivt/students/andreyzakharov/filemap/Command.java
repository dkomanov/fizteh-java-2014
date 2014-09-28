package ru.fizteh.fivt.students.andreyzakharov.filemap;

public interface Command {
    String execute(DbConnector connector, String... args) throws CommandInterruptException;
}
