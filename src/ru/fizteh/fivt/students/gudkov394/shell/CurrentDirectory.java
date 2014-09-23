package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

public class CurrentDirectory {
    private String currentDirectory;

    public CurrentDirectory() {
        currentDirectory = getHome();
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public String getHome() {
        return (new File("").getAbsolutePath());
    }

    public void changeCurrentDirectory(String s) {
        currentDirectory = s;
    }

    
}
