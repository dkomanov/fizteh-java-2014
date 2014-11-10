/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.interpeter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.Command;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.filesystem.CommandParser;

/**
 *
 * @author Shakarim
 */
public class Interpreter {

    private static final String PROMPT = "$ ";

    private final Map<String, Command> supportedCmds;
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;

    public Interpreter(Command[] commands) {
        this(commands, System.in, System.out, System.err);
    }

    public Interpreter(Command[] commands, InputStream in, PrintStream out, PrintStream err) {
        this.supportedCmds = new HashMap<>();
        for (Command cmd : commands) {
            this.supportedCmds.put(cmd.getName(), cmd);
        }
        this.in = in;
        this.out = out;
        this.err = err;
    }

    private int interactiveMode() {
        try (Scanner scanner = new Scanner(in)) {
            out.print(PROMPT);
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                processCommand(command);
                out.print(PROMPT);
            }
            out.println();
        }
        return 0;
    }

    private int batchMode(String[] args) {
        String[] commands = CommandParser.parseArgs(args);
        for (String cmd : commands) {
            if (!processCommand(cmd)) {
                return 1;
            }
        }
        return 0;
    }

    private boolean processCommand(String command) {
        command = command.trim();
        if (command.length() > 0) {
            String cmdName = CommandParser.getCmdName(command);
            if (!supportedCmds.containsKey(cmdName)) {
                err.printf("'%s' Unknown command\n", cmdName);
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
                String msg = "%s: %s: No such File or Directory\n";
                err.printf(msg, cmdName, e.getMessage());
                return false;
            } catch (AccessDeniedException e) {
                String msg = "Cannot perform: %s: %s: Access denied\n";
                err.printf(msg, cmdName, e.getMessage());
                return false;
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                err.printf("%s: %s\n", cmdName, e.getMessage());
                return false;
            }
        }
        return true;
    }

    public int exec(String... args) {
        if (args.length == 0) {
            return interactiveMode();
        } else {
            return batchMode(args);
        }
    }
}
