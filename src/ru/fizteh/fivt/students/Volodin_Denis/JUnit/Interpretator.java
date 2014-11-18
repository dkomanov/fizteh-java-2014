package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import java.util.Scanner;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class Interpretator {
    
    private boolean isInteractive;
    
    public void run(final String[] args, TableProvider tableProvider, Table table) {
        isInteractive = (args == null);
        try (Scanner scanner = new Scanner(System.in)) {
            do {
                String[] input;
                if (isInteractive) {
                    System.out.print("$ ");
                    input = scanner.nextLine().split(";");
                } else {
                    StringBuilder helpArray = new StringBuilder();
                    for (int i = 0; i < args.length; ++i) {
                        helpArray.append(args[i]).append(' ');
                    }
                    input = helpArray.toString().split(";");
                }
                for (int i = 0; i < input.length; ++i) {
                    if (input[i].length() > 0) {
                        String[] buffer = input[i].trim().split("\\s+");
                        try {
                            ParserMultiFileMap.parser(buffer, tableProvider, table);
                        } catch (Exception exception) {
                            System.err.println(exception.getMessage());
                        }
                    }
                }
            } while(isInteractive);
        } catch (Exception exception) {
            System.err.println("Smth wrong: " + exception.getMessage());
            System.exit(ReturnCodes.ERROR);
        }
    }
}
