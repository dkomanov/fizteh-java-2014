package ru.fizteh.fivt.students.dsalnikov.shell;

import java.io.IOException;

public interface Parser {
    String[] parseCommandArguments(String[] stringToParse);

    String[] splitSingleCommandByDelimeter(String cmdString) throws IOException;

    String[] splitStringArgsByDelimeter(String args);

    String getDelimeter();
}
