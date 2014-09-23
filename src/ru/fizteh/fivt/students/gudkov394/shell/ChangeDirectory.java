package ru.fizteh.fivt.students.gudkov394.shell;


import java.io.File;
import java.io.IOException;

//работает
public class ChangeDirectory {
    public ChangeDirectory(String[] currentArgs, CurrentDirectory cd) {
        if (currentArgs.length > 2) {
            System.err.println("extra arguments for cd");
            System.exit(1);
        }
        if (currentArgs.length == 1) {
            cd.changeCurrentDirectory(cd.getHome());
        } else {
            if (currentArgs[1].equals(".")) {

            } else if (currentArgs[1].equals("..")) {
                File f = new File(cd.getCurrentDirectory());
                cd.changeCurrentDirectory(f.getParent());
            } else {
                File f = new File(currentArgs[1]);
                if (!f.isAbsolute()) {
                    f = new File(cd.getCurrentDirectory(), currentArgs[1]);
                    if (!f.exists()) {
                        System.err.println("this directory doesn't exist");
                        System.exit(4);
                    }
                }
                try {
                    cd.changeCurrentDirectory(f.getCanonicalPath());
                } catch (IOException e) {
                    System.err.println("problem with directory");
                    System.exit(1);
                }
            }
        }
    }
}
