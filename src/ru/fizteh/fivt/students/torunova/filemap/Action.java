package ru.fizteh.fivt.students.torunova.filemap;

/**
 * Created by nastya on 08.10.14.
 */
public interface Action {
    boolean run(String[] args, Database db);

    String getName();
}
