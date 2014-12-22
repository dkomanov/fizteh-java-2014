package ru.fizteh.fivt.students.vladislav_korzun.JUnit.Interpreter;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class Interpreter {
    private InputStream in;
    private PrintStream out;
    private Object connector;
    private Map<String, Command> commands;
    
    public Interpreter(Object connector, Command[] commands,
            InputStream in, PrintStream out) throws Exception {
        if (in == null) {
            throw new IllegalArgumentException("Input stream is null");
        } 
        if (out == null) {
            throw new IllegalArgumentException("Output stream is null");
        } 
        this.commands = new HashMap<>();
        this.in = in;
        this.out = out;
        this.connector = connector;
        for (Command cmd : commands) {
            this.commands.put(cmd.getName(), cmd);
        }
    }
    
    public Interpreter(Object connector, Command[] commands) throws Exception {
        this(connector, commands, System.in, System.out);
    }
    
    public void run(String[] args) throws Exception {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }
    
    private void interactiveMode() {
       try (Scanner in = new Scanner(this.in)) {
            List<String[]> inputString = new LinkedList<>();
            String request = new String();
            String commandName = new String();
            String[] args = null;
            do {
                this.out.print("$ ");
                request = in.nextLine();
                inputString = Parser.mainParser(request);
                for (String[] cmd : inputString) {
                    commandName = cmd[0];
                    args = new String[cmd.length - 1];
                    for (int i = 1; i < cmd.length; i++) {
                        args[i - 1] = new String(cmd[i]);
                    }
                    Command command = commands.get(commandName);
                    if (command == null) {
                        System.err.println("Command '" + commandName + "' doesn't supported");

                    } else {
                        try {
                            command.execute(connector, args);
                        } catch (ExecuteException e) {
                            System.err.println(e.getMessage());

                        }
                    }
                }
            } while (!commandName.equals("exit"));
        }
    }
    
    void batchMode(String[] args) {
        List<String[]> inputString = new LinkedList<>();
        String request = new String();
        String commandName = new String();
        for (int i = 0; i < args.length; i++) {
            request += args[i] + " ";
        }
        inputString = Parser.mainParser(request);
        for (String[] cmd : inputString) {
            commandName = cmd[0];
            args = new String[cmd.length - 1];
            for (int i = 1; i < cmd.length; i++) {
                args[i] = new String(cmd[i]);
            }
            Command command = commands.get(commandName);
            try {
                command.execute(connector, args);
            } catch (ExecuteException e) {
                System.err.println(e.getMessage());
            }
        }

    }
    
    static class Parser {

        public static List<String[]> mainParser(String args) {
            List<String[]> answer = new LinkedList<String[]>();
            String[] arg = semicolonParser(args);
            for (int i = 0; i < arg.length; i++) {
                answer.add(spaceParser(arg[i]));
            }
            return answer;
        }
        
        private static String[] semicolonParser(final String arg) {
            String[] answer = null;
            String buffer = new String();
            buffer = arg.trim();    
            answer = buffer.split(";");
            return answer;
        }
        
        private static String[] spaceParser(final String arg) {
            String[] answer = null;
            String buffer = new String();
            buffer = arg.trim();
            answer = buffer.split(" ");
            return answer;
        }
    }
}



