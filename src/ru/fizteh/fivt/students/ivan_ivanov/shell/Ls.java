package ru.fizteh.fivt.students.ivan_ivanov.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Ls implements Command {

    public final String getName() {

        return "ls";
    }

    public final void executeCmd(final Shell shell, final String[] args) throws IOException {

        if (0 == args.length) {
            File currentDirectory = new File(shell.getState().getPath().toString());
            File[] listOfFiles = currentDirectory.listFiles();
            PrintStream print = new PrintStream(System.out);
            if (listOfFiles != null) {
                for (File listOfFile : listOfFiles) {
                    print.println(listOfFile.getName());
                }
            }
        } else {
            throw new IOException("not allowed number of arguments");
        }
    }
}
