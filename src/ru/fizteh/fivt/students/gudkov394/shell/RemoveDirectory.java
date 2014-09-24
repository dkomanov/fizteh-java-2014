package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

//работает
public class RemoveDirectory {
    private void recursiveDelete(File f) {
        if (f.isDirectory()) {
            try {
                for (File tmp : f.listFiles()) {
                    recursiveDelete(tmp);
                }
                f.delete();
            } catch (NullPointerException e2) {
                System.err.println("Sorry, problem with ListFiles in delete_recursive");
                System.exit(3);
            }
        }
        f.delete();
        if (f.exists()) {
            System.err.println("sorry I can't remove this");
        }
    }

    public RemoveDirectory(String[] currentArgs, CurrentDirectory cd) {
        if (currentArgs.length > 3) {
            System.err.println("more then 3 arguments to rm");
            System.exit(1);
        } else if (currentArgs.length == 2) {
            File f = new File(cd.getCurrentDirectory(), currentArgs[1]);
            if (!f.exists()) {
                System.err.println("This directory doesn't exist");
                System.exit(3);
            }
            f.delete();
        } else if ("-r".equals(currentArgs[1])) {
            File f = new File(cd.getCurrentDirectory(), currentArgs[2]);
            if (!f.exists()) {
                System.err.println("This directory doesn't exist");
                System.exit(3);
            }
            recursiveDelete(f);
        } else {
            System.err.println("fig arguments");
            System.exit(2);
        }

    }
}
