package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap2;

/**
 * Created by Aliaksei Semchankau on 14.11.2014.
 */
public class DatabaseFactory {

    public DatabaseProvider create(String dir) {

        if (dir == null) {
            throw new IllegalArgumentException("incorrect direction for DatabaseFactory");
        }

        DatabaseProvider dProvider = new DatabaseProvider(dir);
        return dProvider;
    }
}
