 package ru.fizteh.fivt.students.AliakseiSemchankau.filemap2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Aliaksei Semchankau on 14.11.2014.
 */
public class DatabaseTableMain {

    Boolean needExit;

    public static void main(String[] args) {
        DatabaseTableMain dtm = new DatabaseTableMain();
        dtm.run(args);
    }

    public void run(String[] args) {

        if (System.getProperty("user.dir").isEmpty()) {
            System.out.println("user.dir is incorrect");
        }
        if (System.getProperty("db.file").isEmpty()) {
            System.out.println("db.file is incorrect");
        }

        Path pathToFile = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file"));

        if (!Files.exists(pathToFile)) {
            try {
                Files.createFile(pathToFile);
            } catch (IOException ioexc) {
                throw new DatabaseException(pathToFile + ": can't create such file");
            }
            }

        DatabaseTable dTable = new DatabaseTable(pathToFile);
        dTable.openFile(pathToFile);

        if (args.length == 0) {
            interactiveMode(dTable);
        } else {
            pocketMode(args, dTable);
        }
    }

    public  void interactiveMode(DatabaseTable dTable) {
        Queue<Vector<String>> listOfCommands = new LinkedList<Vector<String>>();
        needExit = false;

        while (!needExit) {
            readCommands(null, listOfCommands);
            doCommands(listOfCommands, dTable);
        }
    }

    public  void pocketMode(String[] args, DatabaseTable dTable) {
        Queue<Vector<String>> listOfCommands = new LinkedList<Vector<String>>();
        needExit = false;
        readCommands(args, listOfCommands);
        doCommands(listOfCommands, dTable);

    }

    public  void readCommands(String[] args, Queue<Vector<String>> listOfCommands) {

        String toParse = "";
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                toParse += (args[i] + " ");
            }
        } else {
            Scanner line = new Scanner(System.in);
            toParse = line.nextLine();
        }

        if (toParse.length() == 0) {
            return;
        }

        String[] argLines = toParse.split(";");
        for (String curArgument : argLines) {
            listOfCommands.add(processing(curArgument));
            }
    }

    public  Vector<String> processing(String argLine) {

        String[] arguments = argLine.split(" ");

        Vector<String> argumentList = new Vector<String>();

        for (String curArg : arguments) {
            if (curArg != null && curArg.length() > 0) {
                argumentList.add(curArg);
            }
        }
        return argumentList;
    }

    public  void doCommands(Queue<Vector<String>> listOfCommands, DatabaseTable dTable) {

        HashMap<String, CommandInterface> commandsHashMap = new HashMap<String, CommandInterface>();

        commandsHashMap.put("get", new CommandGet());
        commandsHashMap.put("list", new CommandList());
        commandsHashMap.put("put", new CommandPut());
        commandsHashMap.put("remove", new CommandRemove());
        commandsHashMap.put("exit", new CommandExit());

        while (!listOfCommands.isEmpty()) {
            Vector<String> args = listOfCommands.poll();

            if (args.isEmpty()) {
                System.out.println("");
                continue;
            }
            //System.out.println(args.elementAt(0));

            if (args.elementAt(0).equals("exit")) {
                needExit = true;
                commandsHashMap.get(args.elementAt(0)).makeCommand(args, dTable);
                return;
            }

            if (commandsHashMap.get(args.elementAt(0)) != null) {
                commandsHashMap.get(args.elementAt(0)).makeCommand(args, dTable);
            } else {
                System.out.println("there is no such command");
            }
        }
    }


}
