package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Aliaksei Semchankau on 09.11.2014.
 */
public class FactoryMain {

    Boolean needExit;

    public static void main(String[] args) {
        FactoryMain fm = new FactoryMain();
        fm.run(args);
    }

    public void run(String[] args) {

        DatabaseFactory factory = new DatabaseFactory();
        if (System.getProperty("user.dir").isEmpty()) {
            System.out.println("user.dir is incorrect");
        }
        if (System.getProperty("fizteh.db.dir").isEmpty()) {
            System.out.println("fizteh.db.dir is incorrect");
        }

        Path pathDirection = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));

        String dir = pathDirection.toString();

        DatabaseProvider dProvider = factory.create(dir);

        if (args.length == 0) {
            System.out.println("interactive");
            interactiveMode(dProvider);
        } else {
            System.out.println("pocket");
            pocketMode(args, dProvider);
        }

    }

    public  void interactiveMode(DatabaseProvider dProvider) {
        Queue<Vector<String>> listOfCommands = new LinkedList<Vector<String>>();
        needExit = false;

        while (!needExit) {
            readCommands(null, listOfCommands);
            doCommands(listOfCommands, dProvider);
        }
    }

    public  void pocketMode(String[] args, DatabaseProvider dProvider) {
        Queue<Vector<String>> listOfCommands = new LinkedList<Vector<String>>();
        needExit = false;
        readCommands(args, listOfCommands);
        doCommands(listOfCommands, dProvider);

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

        String curArgument = "";

        for (int curSymbol = 0; ; ++curSymbol) {
            if (curSymbol != toParse.length() && toParse.charAt(curSymbol) != ';') {
                curArgument += toParse.charAt(curSymbol);
            } else {

                listOfCommands.add(processing(curArgument));

                curArgument = "";
                if (curSymbol == toParse.length()) {
                    break;
                }
            }
        }

       /*while (!listOfCommands.isEmpty())
        {
            Vector<String> curLine = listOfCommands.poll();
            for (int i = 0; i < curLine.size(); ++i)
                System.out.print(curLine.get(i) + " ");
            System.out.println(";");
        }*/
    }

    public  Vector<String> processing(String argLine) {
        Vector<String> argumentList = new Vector<String>();
        String argument = new String();
        argument = "";
        for (int symbol = 0; ; ++symbol) {
            if (symbol != argLine.length() && argLine.charAt(symbol) != ' ') {
                argument += argLine.charAt(symbol);
            } else {
                if (argument.length() > 0) {
                    argumentList.add(argument);
                    argument = "";
                }

                if (symbol == argLine.length()) {
                    break;
                }
            }
        }

        return argumentList;
    }

    public  void doCommands(Queue<Vector<String>> listOfCommands, DatabaseProvider dProvider) {

        HashMap<String, TableInterface> tableCommandsHashMap = new HashMap<String, TableInterface>();
        HashMap<String, CommandInterface> commandsHashMap = new HashMap<String, CommandInterface>();

        tableCommandsHashMap.put("create", new TableCreate());
        tableCommandsHashMap.put("drop", new TableDrop());
        tableCommandsHashMap.put("use", new TableUse());
        tableCommandsHashMap.put("show", new TableShowTables());

        commandsHashMap.put("get", new CommandGet());
        commandsHashMap.put("list", new CommandList());
        commandsHashMap.put("put", new CommandPut());
        commandsHashMap.put("remove", new CommandRemove());
        commandsHashMap.put("commit", new CommandCommit());
        commandsHashMap.put("rollback", new CommandRollBack());

        while (!listOfCommands.isEmpty()) {
            Vector<String> args = listOfCommands.poll();

            //System.out.println(args.elementAt(0));

            if (args.elementAt(0).equals("exit")) {
                needExit = true;
                return;
            }

            if (tableCommandsHashMap.get(args.elementAt(0)) != null) {
                tableCommandsHashMap.get(args.elementAt(0)).makeCommand(args, dProvider);
            } else if (commandsHashMap.get(args.elementAt(0)) != null) {
                if (dProvider.currentTable == null && !args.elementAt(0).equals("exit")) {
                    System.out.println("choose a table at first");
                } else {
                    commandsHashMap.get(args.elementAt(0)).makeCommand(args, dProvider);
                }
            } else {
                System.out.println("there is no such command");
            }
        }
    }


}
