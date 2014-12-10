package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Created by Дмитрий on 27.11.2014.
 */
public class Modes {
    Connector dbConnector;

    public Modes(Path dbPath) {
        try {
            dbConnector = new Connector(dbPath);
        } catch (IOException e) {
            System.err.println("Exception in creating new Connector");
            System.exit(-1);
        }
    }

    private static boolean exitWarning() {
        System.out.println("There are uncommited changes! Do you want to exit without commit?(y/n)");
        Scanner sc = new Scanner(System.in);
        String answer = sc.nextLine();
        while (!answer.equals("y") && !answer.equals("n")) {
            System.out.println("Bad answer, please write 'y' or 'n' without brackets");
            answer = sc.nextLine();
        }
        return answer.equals("y");
    }

    public void batchMode(String[] args) {
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
                while (j < arguments.length && arguments[j].equals("")) {
                    ++j;
                }
                if (j >= arguments.length) {
                    continue;
                }
                if (arguments[j].equals("exit")) {
                    if (!dbConnector.getActiveTable().getNewKey().isEmpty()
                            || !dbConnector.getActiveTable().getRemoved().isEmpty()) {
                        if (exitWarning()) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                Command whatToDo = dbConnector.commands.get(arguments[j]);
                if (whatToDo == null) {
                    System.out.println("Not found command: " + arguments[j]);
                    System.exit(-1);
                    return;
                }
                String[] newArgs = new String[arguments.length - j - 1];
                System.arraycopy(arguments, j + 1, newArgs, 0, newArgs.length);
                dbConnector.run(whatToDo.name, newArgs, true, false);
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
        dbConnector.close();
    }

    public void interactiveMode() {
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
                if (arguments[0].equals("")) {
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
                    if (!dbConnector.run(whatToDo.name, newArgs, false, true)) {
                        break;
                    }
                } else {
                    dbConnector.run(whatToDo.name, newArgs, false, false);
                }
            }
            if (exit) {
                if (dbConnector.getActiveTable() == null) {
                    break;
                }
                if (!dbConnector.getActiveTable().getNewKey().isEmpty()
                        || !dbConnector.getActiveTable().getRemoved().isEmpty()) {
                    if (exitWarning()) {
                        break;
                    }
                    exit = false;
                } else {
                    break;
                }
            }
        }
        dbConnector.close();
    }
}
