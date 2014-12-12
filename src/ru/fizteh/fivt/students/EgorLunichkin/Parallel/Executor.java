package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Executor {
    public Executor(String[] args) throws ExitException {
        String dbPath = System.getProperty("fizteh.db.dir");
        try {
            tableProvider = new ParallelTableProvider(dbPath);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            throw new ExitException(1);
        }
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }

    private ParallelTableProvider tableProvider;

    private void interactiveMode() throws ExitException {
        Scanner in = new Scanner(System.in);
        System.out.print("$ ");
        while (in.hasNextLine()) {
            String[] commands = in.nextLine().trim().split(";");
            for (String command : commands) {
                executeCommand(command);
            }
            System.out.print("$ ");
        }
        in.close();
        System.out.close();
    }

    private void packageMode(String[] args) throws ExitException {
        String[] commands = String.join(" ", args).trim().split(";");
        for (String command : commands) {
            executeCommand(command);
        }
    }

    private void executeCommand(String cmd) throws ExitException {
        String[] command = cmd.trim().split("\\s+");
        try {
            Command exec = CommandManager.parseCommand(tableProvider, command);
            exec.run();
        } catch (ParallelException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            throw new ExitException(ex.getMessage());
        }
    }
}
