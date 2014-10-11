package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 08 October 2014
 * Time: 21:12
 */
public class FileMapRun {
    /**
     * We switch commands into different classes.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    public static void run(final String[] currentArgs, Map currentTable) {
        switch (currentArgs[0]) {
            case "put":
                Put.putRun(currentArgs, currentTable);
                break;
            case "exit":
                Exit.exitRun(currentArgs, currentTable);
                break;
            case "get":
                Get.getRun(currentArgs, currentTable);
                break;
            case "remove":
                Remove.removeRun(currentArgs, currentTable);
                break;
            case "list":
                List.listRun(currentArgs, currentTable);
                break;
            default:
                System.err.println(currentArgs[0] + ": command not found");
                System.exit(1);
                break;
        }
    }

    /**
     * Interactive Mode: do entered functions.
     */
    public static void interactiveMode() {
        Scanner sc = new Scanner(System.in);
        Map currentTable = new HashMap<String, String>();
        Initialization.initialization(currentTable, System.getProperty("db.file"));
        while (true) {
            String currentString = sc.nextLine();
            currentString = currentString.trim();
            run(currentString.split("\\s+"), currentTable);
        }
    }

    /**
     * Check, whether entered command exists or not.
     * @param name Name of command.
     * @return Bool meaning: if command exists - true, otherwise - false.
     */
    public static Boolean checkName(final String name) {
        String[] s = {"put", "exit", "get", "remove", "list"};
        for (String c : s) {
            if (name.equals(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Package mode: we enter command constantly.
     * @param currentArgs Commands that were entered: name, its' arguments.
     */
    public static void batchMode(final String[] currentArgs) {
        Map currentTable = new HashMap<String, String>();
        Initialization.initialization(currentTable, System.getProperty("db.file"));
        StringBuilder builder = new StringBuilder();
        for (String s : currentArgs) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
        string = string.replaceAll("\\s*;\\s*", ";");
        String[] commands = string.split(";|(\\s+)");
        int i = 0;
        while (i < commands.length) {
            int first = i;
            ++i;
            while (i < commands.length && !checkName(commands[i])) {
                ++i;
            }
            int size = 0;
            for (int j = 0; j < i - first; ++j) {
                if (commands[j + first].length() != 0) {
                    ++size;
                }
            }

            String[] s = new String[size];
            int tmpSize = 0;
            for (int j = 0; j < s.length; ++j) {
                if (commands[j + first].length() != 0) {
                    s[tmpSize] = commands[j + first];
                    ++tmpSize;
                }
            }
            run(s, currentTable);
        }
        System.exit(0);
    }

    /**
     * The main function, which do the program: first we see, interactive or package mode is.
     * @param currentArgs Commands that were entered: name, its' arguments.
     */
    public static void fileMapRun(final String[] currentArgs) {
        if (currentArgs.length == 0) {
            interactiveMode();
        } else {
            batchMode(currentArgs);
        }
    }
}
