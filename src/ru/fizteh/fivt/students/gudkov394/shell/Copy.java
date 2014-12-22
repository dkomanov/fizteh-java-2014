package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
/*работает*/

public class Copy {
    private void copyRecursive(final File from, final File to) {
        try {
            CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
            Files.copy(from.toPath(), to.toPath(), options);
        } catch (IOException e2) {
            System.err.println("problem with copy");
            System.exit(3);
        }
        File[] arrayOfFile = null;
        if (from.isDirectory()) {
            try {
                arrayOfFile = from.listFiles();
            } catch (NullPointerException e1) {
                System.err.println("Sorry, problem with ListFiles in copy_recursive");
                System.exit(3);
            }
            if (arrayOfFile != null) {
                for (File f : arrayOfFile) {
                    File newTo = new File(to.getAbsolutePath(), f.getName());
                    copyRecursive(f, newTo);
                }
            }

        }
    }

    public Copy(final String[] currentArgs, final CurrentDirectory cd) {
        if (currentArgs.length != 4 && currentArgs.length != 3) {
            System.out.println("wrong numbers of arguments to cp");
            System.exit(1);
        }
        File from;
        File to;
        if (currentArgs.length == 3) {
            from = new File(currentArgs[1]);
            to = new File(currentArgs[2]);
        } else {
            from = new File(currentArgs[2]);
            to = new File(currentArgs[3]);
        }
        if (!from.isAbsolute()) {
            from = new File(cd.getCurrentDirectory(), from.getPath());
        }
        if (!to.isAbsolute()) {
            to = new File(cd.getCurrentDirectory(), to.getPath());
        }
        if (!from.exists()) {
            System.err.println("this file or derictory doesn't exist");
            System.exit(2);
        }
        if (from.toString().equals(to.toString())) {
            System.err.println("source and destination are equal");
            System.exit(2);
        }
        if (currentArgs.length == 3) {
            if (from.isFile()) {
                try {
                    Files.copy(from.toPath(), to.toPath());
                } catch (IOException e2) {
                    System.err.println("problem with copy");
                    System.exit(3);
                }
            } else if (from.isDirectory()) {
                try {
                    CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
                    Files.copy(from.toPath(), to.toPath(), options);
                } catch (IOException e2) {
                    System.err.println("this file already exists");
                }
            } else {
                System.err.println("fail with copy directory");
                System.exit(2);
            }
        } else if (currentArgs.length == 4 && currentArgs[1].equals("-r")) {
            if (from.isDirectory()) {
                boolean allIsGood = true;
                try {
                    CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
                    Files.copy(from.toPath(), to.toPath(), options);
                } catch (IOException e2) {
                    allIsGood = false;
                    System.err.println("this file already exists");
                }
                if (allIsGood) {
                    copyRecursive(from, to);
                }
            } else {
                System.err.println("fail with copy directory maybe -r is excess");
                System.exit(2);
            }
        } else {
            System.err.println("wrong argument to copy");
        }
    }
}
