package ru.fizteh.fivt.students.dsalnikov.utils;

import java.io.File;

public class FilePathsProvider {

    private byte hashcode;
    private String path;

    public FilePathsProvider(String path, String key) {
        this.hashcode = key.getBytes()[0];
        this.path = path;
    }

    public String getnDir() {
        return String.format("%s.dir", Integer.toString(hashcode % 16));
    }

    public String getnFile() {
        return String.format("%s.dat", Integer.toString((hashcode / 16) % 16));
    }

    public String getPath() {
        return new File(new File(path, getnDir()), getnFile()).toString();
    }
}
