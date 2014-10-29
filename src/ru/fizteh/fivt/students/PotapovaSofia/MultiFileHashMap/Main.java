package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public class CommandHandler<Command, Integer> {
        public Command command;
        public Integer numArgs;

        public CommandHandler(Command cmd, Integer i) {
            command = cmd;
            numArgs = i;
        }
    }

    protected static Map<String, CommandHandler> commandMap;
    public static DataBase db;

    public Main() throws IOException {
        commandMap = new HashMap();
        //Path path = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));
        String rootDirectory = System.getProperty("fizteh.db.dir");
        Path path = Paths.get(rootDirectory);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        } else if (!Files.isDirectory(path)) {
            System.out.println("Database directory (" + path.toString() + ") is a file!");
            System.exit(1);
        }
        db = new DataBase(path);
        commandMap.put("create", new CommandHandler(new CmdCreate(), 2));
        commandMap.put("drop", new CommandHandler(new CmdDrop(), 2));
        commandMap.put("exit", new CommandHandler(new CmdExit(), 1));
        commandMap.put("get", new CommandHandler(new CmdGet(), 2));
        commandMap.put("list", new CommandHandler(new CmdList(), 1));
        commandMap.put("put", new CommandHandler(new CmdPut(), 3));
        commandMap.put("remove", new CommandHandler(new CmdRemove(), 2));
        commandMap.put("show", new CommandHandler(new CmdShow(), 2));
        commandMap.put("use", new CommandHandler(new CmdUse(), 2));
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }

    private static void interactiveMode() throws IOException {
        Main currentCmd = new Main();
        System.out.print("$ ");
        while (true) {
            Scanner in = new Scanner(System.in);
            String str = in.nextLine();
            str = str.trim();
            try {
                currentCmd.exec(str.split("\\s+"));
            } catch (IOException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
            System.out.print("$ ");
        }
    }

    private static void batchMode(String[] args) throws IOException {
        Main currentCmd = new Main();
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
        String[] commands =  string.split("\\s*;\\s*");
        for (String cmd : commands) {
            try {
                currentCmd.exec(cmd.split("\\s+"));
            } catch (IOException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        System.exit(0);
    }

    public static void exec(String[] args) throws IOException {
        if (args.length == 0) {
            throw new RuntimeException("no command");
        }

        CommandHandler commandHandler = commandMap.get(args[0]);
        if (commandHandler == null) {
            throw new IllegalArgumentException("Wrong command");
        } else {
            Command command = (Command) commandHandler.command;
            Integer numArgs = (Integer) commandHandler.numArgs;
            if (numArgs > args.length) {
                throw new IllegalArgumentException(args[0] + ": few arguments");
            } else if (numArgs < args.length) {
                throw new IllegalArgumentException(args[0] + ": too much arguments");
            } else {
                command.execute(new Vector<String>(Arrays.asList(args)), db);
            }
        }
    }
}
