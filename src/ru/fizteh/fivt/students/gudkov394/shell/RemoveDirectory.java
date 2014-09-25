package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

//работает
public class RemoveDirectory {
    private void recursiveDelete(final File f) {
        File[] arrayFile = null;
        if (f.isDirectory()) {
            try {
                arrayFile = f.listFiles();
            } catch (NullPointerException e2) {
                System.err.println("Sorry, problem with ListFiles in delete_recursive");
                System.exit(3);
            }
            if (arrayFile != null) {
                for (File tmp : arrayFile) {
                    recursiveDelete(tmp);
                }
            }
            if (!f.delete()) {
                System.err.println("delete doesn't work");
                System.exit(5);
            }

        }
        if (!f.delete()) {
            System.err.println("delete doesn't work");
            System.exit(5);
        }
        if (f.exists()) {
            System.err.println("sorry I can't remove this");
        }
    }

    public RemoveDirectory(final String[] currentArgs, final CurrentDirectory cd) {
        if (currentArgs.length > 3) {
            System.err.println("more then 3 arguments to rm");
            System.exit(1);
        } else if (currentArgs.length == 2) {
            File f = new File(cd.getCurrentDirectory(), currentArgs[1]);
            if (!f.exists()) {
                System.err.println("This directory doesn't exist");
                System.exit(3);
            }
            if (!f.delete()) {
                System.err.println("delete doesn't work");
                System.exit(5);
            }
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
