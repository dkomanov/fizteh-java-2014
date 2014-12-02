package ru.fizteh.fivt.students.SibgatullinDamir.storable;

/**
 * Created by Lenovo on 20.10.2014.
 */
public interface CommandsForTables {
    void execute(String[] args, MyTableProvider provider) throws MyException;
    String getName();
}
