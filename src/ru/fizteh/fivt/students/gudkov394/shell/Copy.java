package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
/*работает*/

public class Copy {
    private void copyRecursive(File from, File to) {
        try {
            CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
            Files.copy(from.toPath(), to.toPath(), options);
        } catch (IOException e2) {
            System.err.println("problem with copy");
            System.exit(3);
        }

        if (from.isDirectory()) {
            try {
                for (File f : from.listFiles()) {
                    File newTo = new File(to.getAbsolutePath(), f.getName());
                    copyRecursive(f, newTo);
                }
            } catch (NullPointerException e1) {
                System.err.println("Sorry, problem with ListFiles in copy_recursive");
                System.exit(3);
            }
        }
    }

    public Copy(String[] currentArgs, CurrentDirectory cd) {
        if (currentArgs.length > 4) {
            System.out.println("more than 4 arguments to cp");
            System.exit(1);
        }
        File from = new File(currentArgs[1]);
        File to = new File(currentArgs[2]);
        if (!from.isAbsolute()) {
            from = new File(cd.getCurrentDirectory(), currentArgs[1]);
        }
        if (!to.isAbsolute()) {
            to = new File(cd.getCurrentDirectory(), currentArgs[2]);
        }
        if (currentArgs.length == 3) {
            if (from.isFile() && to.isDirectory()) {
                try {
                    File newTo = new File(to.getAbsolutePath(), from.getName());
                    Files.copy(from.toPath(), newTo.toPath());
                } catch (IOException e2) {
                    System.err.println("problem with copy");
                    System.exit(3);
                }
            } else if (from.isDirectory() && to.isDirectory()) {
                File newTo = new File(to.getAbsolutePath(), from.getName());
                try {
                    CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
                    Files.copy(from.toPath(), newTo.toPath(), options);
                } catch (IOException e2) {
                    System.err.println("problem with copy");
                    System.exit(3);
                }
            } else {
                System.err.println("fail with copy directory");
                System.exit(2);
            }
        } else if (currentArgs.length == 4 && currentArgs[1].equals("-r")) {
            if (from.isDirectory() && to.isDirectory()) {
                File newTo = new File(to.getAbsolutePath(), from.getName());
                try {
                    CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
                    Files.copy(from.toPath(), newTo.toPath(), options);
                } catch (IOException e2) {
                    System.err.println("problem with copy");
                    System.exit(3);
                }
                copyRecursive(from, newTo);
            } else {
                System.err.println("fail with copy directory maybe -r is excess");
                System.exit(2);
            }
        } else {
            System.err.println("wrong argument to copy");
        }
    }
}
