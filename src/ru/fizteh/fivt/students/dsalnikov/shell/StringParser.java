package ru.fizteh.fivt.students.dsalnikov.shell;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringParser implements Parser {
    private static final String DELIMETER = " ";

    @Override
    public String[] parseCommandArguments(String[] argsToParse) {
        return Arrays.asList(argsToParse).stream()
                .collect(Collectors.joining(" "))
                .split("\\s*;\\s*");
    }

    @Override
    public String[] splitSingleCommandByDelimeter(String cmdString) {
        return cmdString.trim().split(DELIMETER);
    }

    @Override
    public String[] splitStringArgsByDelimeter(String args) {
        return args.split("\\s*;\\s*");
    }

    public String getDelimeter() {
        return DELIMETER;
    }
}
