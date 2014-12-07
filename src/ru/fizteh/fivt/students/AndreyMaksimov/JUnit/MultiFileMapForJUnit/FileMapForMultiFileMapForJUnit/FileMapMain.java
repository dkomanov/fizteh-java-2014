package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

import java.util.Scanner;

public class FileMapMain {

    public void main(String[] arguments) {

        String needPath = System.getProperty("db.file");
        if (needPath == null) {
            System.err.println("No such path");
            System.exit(1);
        }
        try {
            DataBase needBase = new DataBase(needPath);
            if (arguments.length == 0) {
                interactive(needBase);
            } else {
                batch(needBase, arguments);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }


    private static void interactive(DataBase needBase) {

        Scanner in = new Scanner(System.in);
        boolean exitStatus = false;
        do {
            System.out.print("$ ");
            for (String needString : in.nextLine().split(";\\s*")) {
                try {
                    Command newCommand = Command.fromString(needString);
                    newCommand.startNeedInstruction(needBase);
                } catch (ExitException e) {
                    exitStatus = true;
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.err.flush();
                }
            }
        } while (!exitStatus);
    }

    private static void batch(DataBase needbase, String[] arguments) throws Exception {
        StringBuilder allCommands = new StringBuilder();
        for (String needString : arguments) {
            allCommands.append(needString);
            allCommands.append(' ');
        }
        String[] commands = allCommands.toString().split(";\\s*", 0);
        for (String needString : commands) {
            Command newCommand = Command.fromString(needString);
            try {
                newCommand.startNeedInstruction(needbase);
            } catch (ExitException e) {
                break;
            }
        }
    }
}

