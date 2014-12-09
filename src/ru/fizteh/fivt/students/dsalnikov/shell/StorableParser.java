package ru.fizteh.fivt.students.dsalnikov.shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StorableParser implements Parser {

    static final Pattern SOMETHING_IN_SQUARE_BRACKETS = Pattern.compile("\\[.+\\]");
    static final Pattern SOMETHING_IN_ROUND_BRACKETS = Pattern.compile("\\(.+\\)");
    private static final String delimiter = " ";

    @Override
    public String[] parseCommandArguments(String[] stringToParse) {
        return Arrays.asList(stringToParse).stream().collect(Collectors.joining(" ")).split(";(?![^\\[]*\\])");
    }

    public String[] splitStringArgsByDelimeter(String args) {
        return args.split(";(?![^\\[]*\\])");
    }

    @Override
    public String[] splitSingleCommandByDelimeter(String commandToParse) {
        List<String> cmdAndArgs = new ArrayList<>();
        Scanner cmdScanner = new Scanner(commandToParse);
        cmdAndArgs.add(cmdScanner.next());
        try {
            if (commandToParse.startsWith("put")) {
                try {
                    cmdAndArgs.add(cmdScanner.next());
                    cmdAndArgs.add(cmdScanner.findInLine(SOMETHING_IN_SQUARE_BRACKETS));
                } catch (NullPointerException exc) {
                    throw new IllegalArgumentException("wrong type (execute put: incorrect arguments)");
                }
                if (cmdScanner.hasNext()) {
                    throw new IllegalArgumentException("wrong type (put: wrong amount of arguments)");
                }
            } else if (commandToParse.startsWith("create")) {
                try {
                    cmdAndArgs.add(cmdScanner.next());
                    String temp = cmdScanner.findInLine(SOMETHING_IN_ROUND_BRACKETS)
                            .replaceFirst("\\(", "").replaceFirst("\\)", "");
                    temp = temp.replaceAll("\\s+", " ");
                    if (temp.trim().isEmpty()) {
                        throw new IllegalArgumentException("wrong format: incorrect arguments)");
                    }
                    cmdAndArgs.add(temp);
                } catch (NullPointerException exc) {
                    throw new IllegalArgumentException("wrong format: incorrect arguments");
                }
            } else {
                if (cmdScanner.hasNext()) {
                    cmdAndArgs.addAll(Arrays.asList(cmdScanner.nextLine().split("\\s+")));
                }
            }
        } finally {
            cmdScanner.close();
        }
        return cmdAndArgs.stream().filter(i -> !i.isEmpty()).toArray(String[]::new);
    }

    @Override
    public String getDelimeter() {
        return delimiter;
    }
}
