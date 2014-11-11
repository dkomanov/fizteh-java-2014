package ru.fizteh.fivt.students.andrewzhernov.junit;

interface AbstractHandler {
    Object exec(TableProvider database, String[] args) throws Exception;
    void print(Object returnValue) throws Exception;
}

