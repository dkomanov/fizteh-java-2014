package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;
import java.io.IOException;

public class Cd {
    private Cd() {
        //
    }

    public static void run(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.wait(0);
        } else {
            File resultFile = new File(args[1]);
            File goTo;
            if (resultFile.isAbsolute()) {
                goTo = resultFile.getCanonicalFile();
            } else {
                goTo = new File(CurrentDir.getCurrentDirectory(), args[1]).getCanonicalFile();
            }
            if (goTo.isDirectory()) {
                CurrentDir.changeCurrentDirectory(goTo.getPath());
            } else {
                throw new IOException("cd: '" + args[1] + "': no such file or directory directory");
            }
        }
    }

}
