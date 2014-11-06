package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.util.Map;

public class BatchMode {
    public static void batch(final String[] args, Table table, Map<String, Integer> dbInformation)
            throws Exception {
        try {
            StringBuilder helpArray = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                helpArray.append(args[i]).append(' ');
            }
            String longStr = helpArray.toString();
            String[] input = longStr.split(";");
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
        } catch (Exception exception) {
            System.err.println("Smth wrong: " + exception.getMessage());
            System.exit(ReturnCodes.ERROR);
        }
    }
}
