package ru.fizteh.fivt.students.elina_denisova.multi_file_hash_map;

public class MyTableProviderFactory {


    public MyTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("MyTableProvider.create: " + dir
            + " isn't a directory. ");
        }
        return new MyTableProvider(dir);
    }
}
