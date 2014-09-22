/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author shakarim
 */
public class CpCommand {

    public static void run(String[] args) throws Exception {
        boolean recFlag = false;
        String[] newArgs = new String[3];
        int index = 0;

        for (String opt : args) {
            if (opt.equals("-r")) {
                recFlag = true;
            } else {

                newArgs[index] = opt;
                ++index;
            }
        }

        if (index < 3) {
            throw new Exception(getName() + ": few arguments");
        } else if (index > 3) {
            throw new Exception(getName() + ": too much arguments");
        } else {
            File source = CdCommand.newPath(newArgs[1]);
            File target = CdCommand.newPath(newArgs[2]);

            if (source.equals(target)) {
                throw new Exception(getName() + ": '"
                        + newArgs[1] + "' and '" + newArgs[1]
                        + "' are the same");
            }

            if (!source.exists()) {
                throw new Exception(getName() + ": '"
                        + newArgs[1] + "' No such File or Directory");
            }

            if (source.isDirectory() && !recFlag) {
                throw new Exception(getName() + ": '"
                        + newArgs[1] + "' is Directory");
            }

            if (target.isDirectory() && target.exists()) {
                target = new File(target, source.getName());
            }

            if (!target.exists()) {
                if (source.isDirectory() && !target.mkdir()) {
                    throw new Exception(getName() + ": '"
                            + newArgs[2] + "' No such File or Directory");
                }

                if (source.isFile() && !target.createNewFile()) {
                    throw new Exception(getName() + ": '"
                            + newArgs[2] + "' No such File or Directory");
                }
            }

            copy(source, target);
        }
    }

    public static void copy(File source, File target) throws Exception {
        if (source.isFile()) {
            copyFile(source, target);
        } else {
            copyDir(source, target);
        }
    }

    private static void copyFile(File source, File target) throws Exception {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            CatCommand.readWrite(in, out);
        } catch (Exception e) {
            throw new Exception(getName() + ": Cannot read file");
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    private static void copyDir(File source, File target) throws Exception {
        target.mkdir();
        String[] files = source.list();
        for (String file : files) {
            copy(new File(source, file), new File(target, file));
        }
    }

    public static String getName() {
        return "cp";
    }
}
