package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by deserg on 03.10.14.
 */
public class Main {

    protected Map<String, Command> commandMap;
    protected Queue<ArrayList<String>> argumentsQueue;
    protected Database db;

    public Main() {

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

        Main mainObj = new Main();

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
            if (lineScan.hasNext()) {
                lineStr = lineScan.nextLine();
            } else {
                System.exit(1);
            }
        } else {

            for (String string: args) {
                lineStr += string + " ";
            }

        }

        String[] commandBlockAr = lineStr.split(";");

        for (String commandBlock: commandBlockAr) {
            String[] argsStr = commandBlock.trim().split("\\s+");
            ArrayList<String> argumentVector = new ArrayList<>();

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
            return;
        }

        while (argumentsQueue.size() > 0) {
            ArrayList<String> arguments = new ArrayList<>(argumentsQueue.poll());

            Command command = commandMap.get(arguments.get(0));
            if (command != null) {
                command.execute(arguments, db);
            }
        }


    }

}
