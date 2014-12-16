package ru.fizteh.fivt.students.elina_denisova.shell.commands;

import ru.fizteh.fivt.students.elina_denisova.shell.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;


public class PrintFileCommand implements Command {
    private Shell link;

    public PrintFileCommand(final Shell shell) {
        link = shell;
    }

    @Override
    public void executeCommand(final ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: file");
        }

        for (String currentCommand : arguments) {
            File printFile = null;
            if (Paths.get(currentCommand).isAbsolute()) {
                printFile = new File(currentCommand);
            } else {
                printFile = new File(link.getWorkDirectory(), currentCommand);
            }

            if (!printFile.exists()) {
                throw new Exception(currentCommand + ": doesn't exists");
            }
            if (!printFile.isFile()) {
                throw new Exception(currentCommand + ": not a file");
            }
            if (!printFile.canRead()) {
                throw new Exception(currentCommand + ": permission denied");
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
