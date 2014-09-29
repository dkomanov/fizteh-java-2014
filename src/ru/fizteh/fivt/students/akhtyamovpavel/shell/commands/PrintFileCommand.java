package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class PrintFileCommand implements Command {
    private Shell link;

    public PrintFileCommand(Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.isEmpty()) {
            throw new Exception("usage: cat files ...");
        }
        for (String currentCommand : arguments) {
            File printFile = null;
            if (Paths.get(currentCommand).isAbsolute()) {
                printFile = new File(currentCommand);
            } else {
                printFile = new File(link.getWorkDirectory(), currentCommand);
            }

            if (!printFile.exists()) {
                throw new Exception("cat: " + currentCommand + ": doesn't exists");
            }
            if (!printFile.isFile()) {
                throw new Exception("cat: " + currentCommand + ": not a file");
            }
            if (!printFile.canRead()) {
                throw new Exception("cat: " + currentCommand + ": permission denied");
            }

            String filePath = printFile.getAbsolutePath();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String currentLine = br.readLine();
                while (currentLine != null) {
                    System.out.println(currentLine);
                    currentLine = br.readLine();
                }
            }

        }
    }

    @Override
    public String getName() {
        return "cat";
    }
}
