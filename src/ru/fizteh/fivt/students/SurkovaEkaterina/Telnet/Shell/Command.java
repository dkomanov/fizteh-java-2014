package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell;

import java.io.IOException;
import java.io.PrintStream;

public interface Command<FilesOperations> {

    String getCommandName();

    String getCommandParameters();

    void executeCommand(String parameters, FilesOperations operations, PrintStream out)
            throws IOException;
}
