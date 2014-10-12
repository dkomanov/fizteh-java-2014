package ru.fizteh.fivt.students.SergeyAksenov.FileMap;

public final class Environment {

    public String currentDirectory;
//
    public boolean packageMode;

    public Environment(final String[] args) {
        if (args.length > 0) {
            packageMode = true;
        } else {
            packageMode = false;
        }
        currentDirectory = System.getProperty("user.dir");
    }
}
