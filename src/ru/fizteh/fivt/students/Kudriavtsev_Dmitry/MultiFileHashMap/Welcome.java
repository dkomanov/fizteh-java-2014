package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Created by Дмитрий on 09.10.14.
 */
public class Welcome {

    public static void main(String[] args) {
        Path dbPath;
        try {
            dbPath = new File(System.getProperty("fizteh.db.dir")).toPath().normalize();
        } catch (NullPointerException e) {
            System.err.println("Your directory is null");
            System.exit(-1);
            return;
        }
        Connector dbConnector = new Connector(dbPath);
        if (args.length != 0) {
            String merged = args[0];
            String[] s;
            for (int i = 1; i < args.length; ++i) {
                merged += " ";
                merged += args[i];
            }
            try {
                s = merged.split(";");
                for (String newCommand : s) {
                    int j = 0;
                    String[] arguments = newCommand.split("\\s+");
                    while (arguments[j].equals("")) {
                        ++j;
                    }
                    if (arguments[j].equals("exit")) {
                        break;
                    }
                    Command whatToDo = dbConnector.commands.get(arguments[j]);
                    if (whatToDo == null) {
                        System.out.println("Not found command: " + arguments[j]);
                        System.exit(-1);
                        return;
                    }
                    String[] newArgs = new String[arguments.length - j - 1];
                    System.arraycopy(arguments, j + 1, newArgs, 0, newArgs.length);
                    dbConnector.run(whatToDo.name, newArgs, true);
                }
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                System.exit(-1);
            }
        } else {
            String[] arguments;
            Scanner sc = new Scanner(System.in);
            String s1;
            boolean exit = false;
            while (true) {
                try {
                    System.out.print("$ ");
                    s1 = sc.nextLine();
                } catch (Exception e) {
                    System.err.println("Exception: " + e.getMessage());
                    System.exit(-1);
                    return;
                }
                String[] s = s1.split(";");
                for (String newCommand : s) {
                    while (newCommand.startsWith(" ")) {
                        newCommand = newCommand.substring(1, newCommand.length());
                    }
                    arguments = newCommand.split("\\s+");
                    if (arguments[0].equals("exit")) {
                        exit = true;
                        break;
                    }
                    Command whatToDo = dbConnector.commands.get(arguments[0]);
                    if (whatToDo == null) {
                        System.err.println("Not found command: " + arguments[0]);
                        if (s.length > 1) {
                            break;
                        }
                        continue;
                    }
                    String[] newArgs = new String[arguments.length - 1];
                    if (arguments.length != 0) {
                        System.arraycopy(arguments, 1, newArgs, 0, newArgs.length);
                    }
                    if (s.length > 1) {
                        dbConnector.run(whatToDo.name, newArgs, true);
                    } else {
                        dbConnector.run(whatToDo.name, newArgs, false);
                    }
                }
                if (exit) {
                    break;
                }
            }
        }
        dbConnector.close();
    }
}
