package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

public interface Command {
    String execute(DbConnector connector, String... args) throws CommandInterruptException;
}
