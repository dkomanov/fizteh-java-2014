package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import java.util.Set;


public interface MyTable extends Table {
    String getName();
    void setAutoCommit(boolean status);
    boolean getAutoCommit();
    int getChangesCounter();
    Set<String> list();
    String get(String a);
    String put(String key, String value);
    String remove(String a);
    int size();
    int commit();
    int rollback();
}
