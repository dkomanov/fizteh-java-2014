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
            if (opt.equals("-r") == true) {
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

            if (source.exists() == false) {
                throw new Exception(getName() + ": '"
                        + newArgs[1] + "' No such File or Directory");
            }

            if (target.exists() == false) {
                if (source.isDirectory() == true && target.mkdir() == false) {
                    
                    throw new Exception(getName() + ": '"
                            + newArgs[2] + "' No such File or Directory");
                }

                if (source.isFile() == true 
                        && target.createNewFile() == false) {
                    
                    throw new Exception(getName() + ": '"
                            + newArgs[2] + "' No such File or Directory");
                }
            }

            if (source.isDirectory() == true && recFlag == false) {
                throw new Exception(getName() + ": '"
                        + newArgs[1] + "' is Directory");
            }

            if (target.isDirectory() == true
                    && target.exists() == true) {
                target = new File(target, source.getName());
            }

            Copy(source, target);
        }
    }

    public static void Copy(File source, File target) throws Exception {
        if (source.isFile() == true) {
            CopyFile(source, target);
        } else {
            copyDir(source, target);
        }
    }

    private static void CopyFile(File source, File target) throws Exception {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);
        CatCommand.readWrite(in, out);
        in.close();
        out.close();
    }

    private static void copyDir(File source, File target) throws Exception {
        target.mkdir();
        String[] files = source.list();
        for (String file : files) {
            Copy(new File(source, file), new File(target, file));
        }
    }

    public static String getName() {
        return "cp";
    }
}
