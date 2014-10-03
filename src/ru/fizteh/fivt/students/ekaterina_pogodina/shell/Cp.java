package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;


public class Cp {
    private Cp() {  }
    public static void run(final String[] args, boolean flag, int j) throws IOException {
        if (!(flag && j + 1 >= 4) && !(!flag && j + 1 >= 3)) {
            System.err.println(args[0] + ": missing operand");
        } else {
            if (!(flag && j + 1 <= 4) && !(!flag && j + 1 <= 3)) {
                System.err.println(args[0] + ": too many arguments");
            } else {
                int i;
                if (flag) {
                    i = 2;
                } else {
                    i = 1;
                }
                File source = Utils.absoluteFileCreate(args[i]);
                File destination = Utils.absoluteFileCreate(args[i + 1]);
                if (source.exists()) {
                    if ((destination.exists()) || ((source.isFile())
                            && ((new File(destination.getParent())).exists()))) {
                        Utils.copyFileOrDirectory(source, destination, flag);
                    } else {
                        System.err.println("cp: " + args[i] + " not exists");
                    }
                } else {
                    System.err.println("cp: " + args[i] + " not exists");
                }
            }
        }
    }

}
