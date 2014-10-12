package ru.fizteh.fivt.students.IvanShafran.shell.commands;

import ru.fizteh.fivt.students.IvanShafran.shell.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class CommandCAT extends Command {
    public Shell shellLink;

    public CommandCAT(Shell shell) {
        shellLink = shell;
    }

    @Override
    public void checkArgs(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing operand");
        }
    }

    @Override
    public void execute(ArrayList<String> args) throws Exception {
        checkArgs(args);

        for (String fileName : args) {
            File inputFile;
            inputFile = new File(getAbsolutePath(shellLink.getWorkingDirectory().toString(), fileName));

            if (!inputFile.isFile()) {
                throw new Exception(fileName + ": Not a file");
            }

            if (!inputFile.canRead()) {
                throw new Exception(fileName + ": Permission denied");
            }

            String path = inputFile.getAbsolutePath();
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                String line = br.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = br.readLine();
                }
            }
        }
    }

}
