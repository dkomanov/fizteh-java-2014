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
    private static Map<String, Command> commandMap = new HashMap<>();

    static {
        commandMap.put("exit", new Exit());
        commandMap.put("put", new Put());
        commandMap.put("get", new Get());
        commandMap.put("remove", new Remove());
        commandMap.put("list", new List());
    }

    /**
     * We switch commands into different classes.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    public static void run(final String[] currentArgs, Map<String, String> currentTable) {
        Command command = commandMap.get(currentArgs[0]);
        if (command == null) {
            System.err.println(currentArgs[0] + ": command not found");
            System.exit(1);
        } else {
            command.execute(currentArgs, currentTable);
        }
    }

    /**
     * Interactive Mode: do entered functions.
     */
    public static void interactiveMode() {
        Scanner sc = new Scanner(System.in);
        Map<String, String> currentTable = new HashMap<>();
        Initialization.initialization(currentTable, System.getProperty("db.file"));
        while (true) {
            String currentString = sc.nextLine();
            currentString = currentString.trim();
            run(currentString.split("\\s+"), currentTable);
        }
    }

    /**
     * Package mode: we enter command constantly.
     * @param currentArgs Commands that were entered: name, its' arguments.
     */
    public static void batchMode(final String[] currentArgs) {
        Map<String, String> currentTable = new HashMap<>();
        Initialization.initialization(currentTable, System.getProperty("db.file"));
        StringBuilder builder = new StringBuilder();
        for (String s : currentArgs) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
        String[] commands = string.split("\\s*;\\s*");
        for (String commandParams : commands) {
            run(commandParams.split("\\s+"), currentTable);
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

    /**
     * Checking whether the arguments have normal count.
     * @param length The sum of every argument.
     * @param requiredLength The correct count.
     */
    public static void checkArguments(final int length, final int requiredLength) {
        if (length != requiredLength) {
            System.err.println("put: " + (length < requiredLength ? "not enough" : "too many") + " arguments");
            System.exit(1);
        }
    }
}
