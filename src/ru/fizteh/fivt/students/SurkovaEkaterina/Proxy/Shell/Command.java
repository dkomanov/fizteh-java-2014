package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Shell;

import java.io.IOException;

public interface Command<FilesOperations> {

    String getCommandName();

    String getCommandParameters();

    void executeCommand(String parameters, FilesOperations operations)
            throws IOException;
}
