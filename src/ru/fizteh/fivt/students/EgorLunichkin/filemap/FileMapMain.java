package ru.fizteh.fivt.students.EgorLunichkin.filemap;

public class FileMapMain {
    public static void main(String[] args) {
        System.setProperty("db.file", "db.txt");
        new Executor(args);
        System.out.close();
    }
}
