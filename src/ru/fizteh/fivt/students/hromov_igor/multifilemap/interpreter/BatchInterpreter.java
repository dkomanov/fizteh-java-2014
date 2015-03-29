package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;


import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.exception.ExitCommandException;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class BatchInterpreter {

    private static String inputBegin = "$ ";
    private static String spaceDelete = "\\s+";

    public static void run(HashMap<String, BaseCommand> listCommands) {
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                System.out.print(inputBegin);
                String[] commands = in.nextLine().trim().split(spaceDelete);
                for (int i = 0; i < commands.length; ++i) {
                    commands[i].trim();
                }
                if (commands[0].equals("show")) {
                    commands[0] += "_" + commands[1];
                }
                Parser.parseAndExecute(commands, listCommands);
            }
        } catch (ExitCommandException e) {
            in.close();
            System.exit(0);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
        }
        in.close();
    }
}
