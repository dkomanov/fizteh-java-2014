package ru.fizteh.fivt.students.andrewzhernov.junit;

public interface HandlerInterface {
    Object execute(TableProvider database, String[] args) throws Exception;
    void handle(Object object) throws Exception;
}

