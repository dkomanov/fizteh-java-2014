package ru.fizteh.fivt.students.deserg.storable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.deserg.storable.commands.*;

import java.util.*;

/**
 * Created by deserg on 03.10.14.
 */
public class Main {

    private Map<String, Command> commandMap = new HashMap<>();
    private Queue<ArrayList<String>> argumentsQueue = new LinkedList<>();
    private TableProvider db;

    public Main() {

        String dbDir = System.getProperty("fizteh.db.dir");


        DbTableProviderFactory factory = new DbTableProviderFactory();
        db = factory.create(dbDir);

        commandMap.put("exit", new TableExit());
        commandMap.put("get", new TableGet());
        commandMap.put("list", new TableList());
        commandMap.put("put", new TablePut());
        commandMap.put("remove", new TableRemove());
        commandMap.put("size", new TableSize());
        commandMap.put("commit", new TableCommit());
        commandMap.put("rollback", new TableRollback());
        commandMap.put("create", new DbCreate());
        commandMap.put("drop", new DbDrop());
        commandMap.put("use", new DbUse());
        commandMap.put("show", new DbShowTables());



    }

    public static void main(String[] args) {

        Main shell = new Main();

        if (args.length == 0) {

            while (true) {
                shell.readCommands(null);
                try {
                    shell.executeAll();
                } catch (MyException | IllegalArgumentException | IllegalStateException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }
            }

        } else {

            shell.readCommands(args);
            try {
                shell.executeAll();
            } catch (MyException | IllegalArgumentException | IllegalStateException ex) {
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
                command.execute(arguments, (DbTableProvider) db);
            }
        }


    }

}
