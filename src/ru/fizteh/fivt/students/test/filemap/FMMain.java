package ru.fizteh.fivt.students.deserg.filemap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by deserg on 03.10.14.
 */

public class FMMain {

    protected Map<String, Command> commandMap;
    protected Queue<ArrayList<String>> argumentsQueue;
    protected FileMap fileMap;

    public FMMain() {

        commandMap = new HashMap();
        argumentsQueue = new LinkedList<>();

        String property = System.getProperty("db.file");
        if (property != null) {
            Path path = Paths.get(System.getProperty("user.dir")).resolve(property);
            fileMap = new FileMap(path);
        } else {
            System.out.println("The property has not been set!");
            System.exit(1);
        }


        commandMap.put("exit", new CommandExit());
        commandMap.put("get", new CommandGet());
        commandMap.put("list", new CommandList());
        commandMap.put("put", new CommandPut());
        commandMap.put("remove", new CommandRemove());

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
                command.execute(arguments, fileMap);
            }
        }


    }

}
