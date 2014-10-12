/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.Command;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.CommandParser;

/**
 *
 * @author Shakarim
 */
public class Shell {

    private final Map<String, Command> supportedCmds;
    private final String[] args;

    public Shell(Command[] commands, String[] args) {
        this.supportedCmds = new HashMap<>();
        for (Command cmd : commands) {
            this.supportedCmds.put(cmd.getName(), cmd);
        }
        this.args = args;
    }

    private void interactiveMode() {
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("$ ");
            while (input.hasNextLine()) {
                String command = input.nextLine();
                processCommand(command);
                System.out.print("$ ");
            }
            System.out.println();
        }
        processCommand("exit");
    }

    private void batchMode() {
        String[] commands = CommandParser.parseArgs(args);
        int exitValue = 0;

        for (String cmd : commands) {
            if (!processCommand(cmd)) {
                exitValue = 1;
            }
        }
        System.exit(exitValue);
    }

    private boolean processCommand(String command) {
        command = command.trim();
        if (command.length() > 0) {
            String cmdName = CommandParser.getCmdName(command);
            if (!supportedCmds.containsKey(cmdName)) {
                System.err.printf("'%s' Unknown command\n", cmdName);
                return false;
            }
            try {
                String[] params = CommandParser.getParams(command);
                boolean rec = CommandParser.isRecursive(command);
                int opt = (rec ? 1 : 0);
                int argsNum = supportedCmds.get(cmdName).getArgsNum();

                if (params.length != argsNum + opt) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }

                supportedCmds.get(cmdName).exec(params);
            } catch (FileNotFoundException | NoSuchFileException e) {
                String msg = "%s: '%s' no such File or Directory\n";
                System.err.printf(msg, cmdName, e.getMessage());
                return false;
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                System.err.printf("%s: %s\n", cmdName, e.getMessage());
                return false;
            }
        }
        return true;
    }

    public void exec() {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode();
        }
    }
}
