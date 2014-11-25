package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

public interface Command {
    String execute(DbConnector connector, String... args) throws CommandInterruptException;
}
