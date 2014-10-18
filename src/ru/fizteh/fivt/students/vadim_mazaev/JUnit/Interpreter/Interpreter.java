package ru.fizteh.fivt.students.vadim_mazaev.JUnit.Interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ru.fizteh.fivt.students.vadim_mazaev.JUnit.ExitException;

public final class Interpreter {
    CommandConnector connector;
    Map<String, Command> commands;
    
    public Interpreter(Command[] commands) {
        this.commands = new HashMap<>();
        for (Command cmd : commands) {
            this.commands.put(cmd.getName(), cmd);
        }
    }
    
    public void run(String[] args) throws Exception {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }
    
    private void batchMode(String[] args) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (String current : args) {
            builder.append(current);
            builder.append(" ");
        }
        String[] cmds = builder.toString().split(";");
        for (String current : cmds) {
            parse(current.trim().split("\\s+"));
        }
        //TODO do it in main: ExitCommand.execute(manager);
    }

    private void interactiveMode() throws Exception {
        String[] cmds;
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                cmds = in.nextLine().trim().split(";");
                for (String current : cmds) {
                    parse(current.trim().split("\\s+"));
                }
            }
        } catch (NoSuchElementException e) {
            //TODO 0 or 1
            throw new ExitException(0);
        }
    }
    
    private void parse(String[] cmdWithArgs) throws Exception {
        //TODO catch exceptions
        if (cmdWithArgs.length > 0 && !cmdWithArgs[0].isEmpty()) {
            String commandName = cmdWithArgs[0];
            Command command = commands.get(commandName);
            if (command == null) {
                System.out.println("No such command declared: " + commandName);
            } else {
                String[] args = Arrays.copyOfRange(cmdWithArgs, 1, cmdWithArgs.length);
                command.execute(connector, args);
            }
        }
    }
}
