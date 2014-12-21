package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.FileMapShellOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandCommit extends ACommand<FileMapShellOperations> {
    public CommandCommit() {
        super("commit", "commit");
    }

    public void executeCommand(String params, FileMapShellOperations state) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException("commit: Too many arguments!");
        }
        if (state.table == null) {
            System.err.println("commit: No table!");
            return;
        }
        state.table.commit();
    }
}
