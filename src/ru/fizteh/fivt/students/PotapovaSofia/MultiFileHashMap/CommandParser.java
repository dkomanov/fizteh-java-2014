package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CommandParser {
    protected static Map<String, Command> commandMap;
    public static Queue<Vector<String>> argumentsQueue;
    public static DataBase db;

    public CommandParser() throws IOException {
        commandMap = new HashMap();
        argumentsQueue = new LinkedList<>();
        Path path = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException ex) {
                System.out.println("Unable to create database directory (" + path.toString() + ")");
            }
        } else if (!Files.isDirectory(path)) {
            System.out.println("Database directory (" + path.toString() + ") is a file!");
            System.exit(1);
        }
        db = new DataBase(path);
        commandMap.put("create", new CmdCreate());
        commandMap.put("drop", new CmdDrop());
        commandMap.put("exit", new CmdExit());
        commandMap.put("get", new CmdGet());
        commandMap.put("list", new CmdList());
        commandMap.put("put", new CmdPut());
        commandMap.put("remove", new CmdRemove());
        commandMap.put("show", new CmdShow());
        commandMap.put("use", new CmdUse());
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }

    private static void interactiveMode() throws IOException {
        CommandParser currentCmd = new CommandParser();
        System.out.print("$ ");
        while (true) {
            Scanner in = new Scanner(System.in);
            String str = in.nextLine();
            currentCmd.addToQueue(str);
            try {
                currentCmd.exec();
            } catch (IOException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
            System.out.print("$ ");
        }
    }

    private static void batchMode(String[] args) throws IOException {
        CommandParser currentCmd = new CommandParser();
        String str = "";
        for (String s : args) {
            str += s + " ";
        }
        currentCmd.addToQueue(str);
        try {
            currentCmd.exec();
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void addToQueue(String args) {
        String[] commands = args.split(";");
        for (String cmd : commands) {
            String[] argsStr = cmd.trim().split("\\s+");
            Vector<String> argsVector = new Vector<>();
            for (String arg: argsStr) {
                argsVector.add(arg);
            }
            if (argsVector.size() > 0) {
                argumentsQueue.add(argsVector);
            }
        }
    }

    public void exec() throws IOException {
        if (argumentsQueue.size() == 0) {
            wrongCmd();
        }
        while (argumentsQueue.size() > 0) {
            Vector<String> arguments = new Vector<>(argumentsQueue.poll());
            Command command = commandMap.get(arguments.get(0));
            if (command == null) {
                wrongCmd();
            } else {
                command.execute(arguments, db);
            }
            try {
                db.writeToDataBase();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }

    public static void tooMuchArgs(String cmd) throws IllegalArgumentException {
        throw new IllegalArgumentException(cmd + ": too much arguments");
    }

    public static void fewArgs(String cmd) throws IllegalArgumentException {
        throw new IllegalArgumentException(cmd + ": few arguments");
    }

    public static void wrongCmd() throws IllegalArgumentException {
        throw new IllegalArgumentException("Wrong command");
    }
}
