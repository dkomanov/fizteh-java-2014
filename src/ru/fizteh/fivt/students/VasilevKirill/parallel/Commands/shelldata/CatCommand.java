package ru.fizteh.fivt.students.VasilevKirill.parallel.Commands.shelldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Kirill on 24.09.2014.
 */
public class CatCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (args.length != 2) {
            return 1;
        }
        File file = new File(Shell.currentPath + File.separator + args[1]);
        if (!file.exists()) {
            System.out.println("cat: " + args[1] + ": No such file or directory");
            return 1;
        }
        try (FileInputStream reader = new FileInputStream(file)) {
            byte[] b = new byte[256];
            while (reader.available() > 0) {
                reader.read(b);
                for (byte i : b) {
                    System.out.print((char) i);
                }
            }
        }
        System.out.println("");
        return 0;
    }

    @Override
    public String toString() {
        return "cat";
    }

    @Override
    public boolean checkArgs(String[] args) {
        return false;
    }
}
