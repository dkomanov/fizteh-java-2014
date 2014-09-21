/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author shakarim
 */
public class CatCommand {

    public static void run(String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception(getName() + ": few arguments");
        } else if (args.length > 2) {
            throw new Exception(getName() + ": too much arguments");
        } else {
            File file = CdCommand.newPath(args[1]);
            if (file.isFile() == true) {
                InputStream in;
                try {
                    in = new FileInputStream(file);
                    readWrite(in, System.out);
                    in.close();
                } catch (Exception e) {
                    throw new Exception(getName() + ": Cannot read File");
                }
            } else if (file.isDirectory() == true) {
                throw new Exception(getName() + ": '"
                        + args[1] + "' is Directory");
            } else {
                throw new Exception(getName() + ": '"
                        + args[1] + "' No such File or Directory");
            }
        }
    }

    public static void readWrite(InputStream in,
            OutputStream out) throws Exception {

        int lengthRead;
        byte[] buffer = new byte[4096];
        while ((lengthRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, lengthRead);
        }
    }

    public static String getName() {
        return "cat";
    }
}
