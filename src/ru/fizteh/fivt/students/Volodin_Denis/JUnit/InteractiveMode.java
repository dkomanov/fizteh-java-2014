package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import java.util.Scanner;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class InteractiveMode {
    public static void interactive(TableProvider tables, Table table) {
        Scanner scanner = new Scanner(System.in);
        try {
            do {
                System.out.print("$ ");
                String[] input = scanner.nextLine().split(";");
                for (int i = 0; i < input.length; ++i) {
                    if (input[i].length() > 0) {
                        String[] buffer = input[i].trim().split("\\s+");
                        try {
                            ParserMultiFileMap.parser(buffer, tables, table);
                        } catch (Exception exception) {
                            System.err.println(exception.getMessage());
                        }
                    }
                }
            } while(true);
        } catch (Exception exception) {
            System.err.println("Smth wrong: " + exception.getMessage());
            scanner.close();
            System.exit(ReturnCodes.ERROR);
        }
        scanner.close();
    }
}
