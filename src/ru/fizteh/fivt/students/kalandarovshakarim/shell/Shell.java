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
import java.util.Scanner;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.Command;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.CommandParser;

/**
 *
 * @author Shakarim
 * @param <State>
 */
public class Shell<State> {

    private HashMap<String, Command> supportedCmds;
    private State shellState;
    private final String[] args;

    public Shell() {
        this.args = new String[0];
        this.supportedCmds = new HashMap<>();
    }

    public Shell(State shellState, String[] args, Command<?>[] commands) {
        this.shellState = shellState;
        this.args = args;
        this.supportedCmds = new HashMap<>();
        for (Command<?> cmd : commands) {
            this.supportedCmds.put(cmd.getName(), cmd);
        }
    }

    public void interactiveMode() {
        Scanner input = new Scanner(System.in);
        System.out.print("$ ");
        while (input.hasNextLine()) {
            String command = input.nextLine();
            processCommand(command);
            System.out.print("$ ");
        }
        System.out.println();
    }

    public void packageMode() {
        String[] commands = CommandParser.parseArgs(args);
        int exitVal = 0;

        for (String cmd : commands) {
            if (!processCommand(cmd)) {
                exitVal = 1;
            }
        }
        System.exit(exitVal);
    }

    public boolean processCommand(String command) {
        command = command.trim();
        if (command.length() > 0) {
            String cmdName = CommandParser.getCmdName(command);
            if (!supportedCmds.containsKey(cmdName)) {
                System.err.printf("Shell: '%s' Unknown command\n", cmdName);
                return false;
            }
            try {
                supportedCmds.get(cmdName).exec(shellState, command);
            } catch (FileNotFoundException | NoSuchFileException e) {
                String msg = "%s: '%s' no such File or Directory\n";
                System.err.printf(msg, cmdName, e.getMessage());
                return false;
            } catch (IOException e) {
                System.err.printf("%s: %s\n", cmdName, e.getMessage());
                return false;
            }
        }
        return true;
    }

    public void exec() {
        if (args.length == 0) {
            this.interactiveMode();
        } else {
            this.packageMode();
        }
    }
}
