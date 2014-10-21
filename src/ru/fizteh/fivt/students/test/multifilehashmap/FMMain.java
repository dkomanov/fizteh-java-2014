package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by deserg on 03.10.14.
 */

public class FMMain {

    protected static Map<String, Command> commandMap;
    protected static Queue<Vector<String>> argumentsQueue;
    protected static Database db;

    public FMMain() {

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

        db = new Database(path);

        commandMap.put("exit", new TableExit());
        commandMap.put("get", new TableGet());
        commandMap.put("list", new TableList());
        commandMap.put("put", new TablePut());
        commandMap.put("remove", new TableRemove());
        commandMap.put("create", new DbCreate());
        commandMap.put("drop", new DbDrop());
        commandMap.put("use", new DbUse());
        commandMap.put("show", new DbShowTables());



    }

    public static void main(String[] args) {

        FMMain mainObj = new FMMain();

        if (args.length == 0) {

            while (true) {
                mainObj.readCommands(null);
                try {
                    mainObj.executeAll();
                } catch (MyException ex) {
                    System.out.println(ex.getMessage());
                }
            }

        } else {

            mainObj.readCommands(args);
            try {
                mainObj.executeAll();
            } catch (MyException ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }


        }


    }



    public void readCommands(String[] args) {

        System.out.print("$ ");

        String lineStr = "";

        if (args == null) {
            Scanner lineScan = new Scanner(System.in);
            lineStr = lineScan.nextLine();
        } else {

            for (String string: args) {
                lineStr += string + " ";
            }

        }

        String[] commandBlockAr = lineStr.split(";");

        for (String commandBlock: commandBlockAr) {
            String[] argsStr = commandBlock.trim().split("\\s+");
            Vector<String> argumentVector = new Vector<>();

            for (String arg: argsStr) {
                argumentVector.add(arg);
            }

            if (argumentVector.size() > 0) {
                argumentsQueue.add(argumentVector);
            }
        }

    }

    public void executeAll() {
        if (argumentsQueue.size() == 0) {
            throw new MyException("No command");
        }

        while (argumentsQueue.size() > 0) {
            Vector<String> arguments = new Vector<>(argumentsQueue.poll());

            Command command = commandMap.get(arguments.get(0));
            if (command == null) {
                throw new MyException("Wrong command");
            } else {
                command.execute(arguments, db);
            }
        }


    }

}
