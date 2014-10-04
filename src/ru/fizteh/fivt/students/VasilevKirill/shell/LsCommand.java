package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kirill on 24.09.2014.
 */
public class LsCommand implements Command {
    @Override
    public String toString() {
        return "ls";
    }

    @Override
    public int execute(String[] args) throws IOException {
        if (args.length > 1) {
            return 1;
        }
        File[] listFiles = new File(Shell.currentPath).listFiles();
        for (File f : listFiles) {
            System.out.println(f.getName());
        }
        return 0;
    }
}
