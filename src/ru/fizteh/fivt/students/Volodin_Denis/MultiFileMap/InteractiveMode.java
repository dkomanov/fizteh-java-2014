package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.util.HashMap;
import java.util.Scanner;

public class InteractiveMode {
    public static void interactive(Table table, HashMap<String, Integer> dbInformation) {
        Scanner scanner = new Scanner(System.in);
        try {
            do {
                System.out.print("$ ");
                String[] input = scanner.nextLine().split(";");
                for (int i = 0; i < input.length; ++i) {
                    if (input[i].length() > 0) {
                        String[] buffer = input[i].trim().split("\\s+");
                        try {
                            ParserMultiFileMap.parser(buffer, table, dbInformation);
                        } catch (Exception exception) {
                            System.err.println(exception.getMessage());
                        }
                    }
                }
            } while(true);
        } catch (Exception exception) {
            System.err.println("Smth wrong.");
            scanner.close();
            System.exit(ReturnCodes.ERROR);
        }
        scanner.close();
    }
}
