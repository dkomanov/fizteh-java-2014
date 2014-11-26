package ru.fizteh.fivt.students.SibgatullinDamir.Shell;

/**
 * Created by Lenovo on 01.10.2014.
 */
public interface Commands {
    void execute(String[] args) throws MyException;
    String getName();
}
