package ru.fizteh.fivt.students.deserg.filemap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by deserg on 03.10.14.
 */

public class FMMain {

    protected static Map<String, Command> commandMap;
    protected static Queue<Vector<String>> argumentsQueue;
    protected static FileMap fileMap;

    public FMMain() {

        commandMap = new HashMap();
        argumentsQueue = new LinkedList<>();

        Path path = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file"));
        fileMap = new FileMap(path);

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
            lineStr = lineScan.nextLine();
        } else {

            for (String string: args) {
                lineStr += string + " ";
            }

        }


        Scanner blockScan = new Scanner(lineStr);

        blockScan.useDelimiter(";");
        while (blockScan.hasNext()) {
            String commandBlock = blockScan.next();

            Scanner argScan = new Scanner(commandBlock);
            Vector<String> argumentVector = new Vector<>();
            argScan.useDelimiter("\\s+");

            while (argScan.hasNext()) {
                String nextArg = argScan.next();
                if (!nextArg.equals("")) {
                    argumentVector.add(nextArg);
                }
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
                throw new MyException("No command");
            } else {
                command.execute(arguments, fileMap);
            }
        }


    }

}
