package ru.fizteh.fivt.students.Soshilov.shell;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 02 October 2014
 * Time: 18:24
 */
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
        //Returns the absolute pathname string of pre-this abstract pathname.
    }

    public void changeCurrentDirectory(final String s) {
        currentDirectory = s;
    }

}
