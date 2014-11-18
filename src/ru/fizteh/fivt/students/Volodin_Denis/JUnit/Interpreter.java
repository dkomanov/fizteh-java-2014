package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import java.util.Scanner;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class Interpreter {
    
    private final String PROMT = "$ ";
    private final String STATEMENT_DELIMITER = ";";
    private final String PARAM_REGEXP = "\\s+";
    
    private void executeLine(final String[] input, TableProvider tableProvider, Table table) {
        for (int i = 0; i < input.length; ++i) {
            if (input[i].length() > 0) {
                String[] buffer = input[i].trim().split(PARAM_REGEXP);
                try {
                    ParserMultiFileMap.parser(buffer, tableProvider, table);
                } catch (Exception exception) {
                    System.err.println(exception.getMessage());
                }
            }
        }
    }
    
    public void run(final String[] args, TableProvider tableProvider, Table table) {
        while (args.length == 0) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print(PROMT);
                String[] input = scanner.nextLine().split(STATEMENT_DELIMITER);
                executeLine(input, tableProvider, table);
            } catch (Throwable exception) {
                System.err.println("Smth wrong: " + exception.getMessage());
                System.exit(ReturnCodes.ERROR);
            }
        }
        StringBuilder helpArray = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            helpArray.append(args[i]).append(' ');
        }
        String[] input = helpArray.toString().split(STATEMENT_DELIMITER);
        executeLine(input, tableProvider, table);
    }
}
