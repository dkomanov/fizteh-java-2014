package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap;

public interface Command {
    String execute(DbConnector connector, String... args) throws CommandInterruptException;
}
