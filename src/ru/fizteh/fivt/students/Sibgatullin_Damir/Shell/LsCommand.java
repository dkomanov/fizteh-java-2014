package ru.fizteh.fivt.students.Sibgatullin_Damir.Shell;

import java.io.File;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class LsCommand implements Commands {
    public void execute(String[] args) throws MyException {
        if (args.length != 1) { throw new MyException("ls: too many arguments"); }
        File currentDir = new File(Shell.currentPath.toString());
        File[] files = currentDir.listFiles();
        for (File file: files) {
            System.out.println(file.getName());
            }
        }

    public String getName() {
        return "ls";
    }
}
