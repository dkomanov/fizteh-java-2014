package ru.fizteh.fivt.students.SergeyAksenov.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CatCommand implements Command {
    public final void run(final String[] args, final Environment env)
            throws ShellException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            ErrorHandler.countArguments("cat");
        }
        File fileToPrint = new File(env.currentDirectory
                + File.separator + args[1]);
        if (fileToPrint.exists() && fileToPrint.isDirectory()) {
            ErrorHandler.isDirectory("cat", args[1]);
        }
        try {
            try (BufferedReader reader =
                         new BufferedReader(new FileReader(fileToPrint))) {
                String str;
                while ((str = reader.readLine()) != null) {
                    System.out.println(str);
                }
                reader.close();
            }
        } catch (IOException e) {
            ErrorHandler.noFile("cat", args[1]);
        }
    }
}
