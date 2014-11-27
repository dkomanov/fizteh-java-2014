package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.IOException;

public class CommandDir extends ACommand<FilesOperations> {

    public CommandDir() {
        super("dir", "dir");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations)
            throws IOException {
        if (parameters.length() > 1) {
            throw new IllegalArgumentException("dir: too many arguments!");
        }

        String[] filesList = operations.getFilesList();
        if (filesList == null) {
            return;
        }
        for (String file:filesList) {
            System.out.print(file);
        }
    }

}
