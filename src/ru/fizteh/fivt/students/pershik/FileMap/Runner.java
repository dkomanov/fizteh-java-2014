package ru.fizteh.fivt.students.pershik.FileMap;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by pershik on 10/4/14.
 */
public class Runner {

    protected boolean exited;
    protected boolean batchMode;

    protected boolean checkArguments(int min, int max, int real) {
        return min <= real && real <= max;
    }

    protected void errorCntArguments(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": invalid number of arguments");
    }

    protected void errorUnknownCommand(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": unknown command");
    }

    protected String[] parseCommand(String command) {
        return command.trim().split("\\s+");
    }

    protected void execute(String command) {
    }

    public void runCommands(String[] commands) {
        for (String command : commands) {
            execute(command);
            if (exited) {
                break;
            }
        }
    }

    private void execLine(String line) {
        String[] commands = line.trim().split(";");
        runCommands(commands);
    }

    public void runInteractive() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                if (!sc.hasNextLine()) {
                    break;
                }
                String allCommands = sc.nextLine();
                execLine(allCommands);
                if (exited) {
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void runBatch(String[] args) {
        batchMode = true;
        StringBuilder allCommands = new StringBuilder();
        for (String arg : args) {
            allCommands.append(arg);
            allCommands.append(" ");
        }
        execLine(allCommands.toString());
    }
}
