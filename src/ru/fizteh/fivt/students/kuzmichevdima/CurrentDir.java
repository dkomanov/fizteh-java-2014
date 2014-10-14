/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

import java.io.File;

public class CurrentDir {
    private String currentDir;

    public CurrentDir() {
        currentDir = getHome();
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public String getHome() {
        return new File("").getAbsolutePath();
    }

    public void changeCurrentDir(final String s) {
        currentDir = s;
    }
}
