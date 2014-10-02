package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

import ru.fizteh.fivt.students.LebedevAleksey.Shell01.CommandParser;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.Log;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParsedCommand;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParserException;

import java.util.List;

public class FileMap extends CommandParser {
    Table currentTable = new Table();

    public FileMap() {
        super();
        if (System.getProperty(TablePart.DB_FILE_PARAMETER_NAME) == null) {
            System.err.println("Database file name should be set");
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        FileMap map = new FileMap();
        map.run(args);
        Log.closeStream();
    }

    @Override
    protected boolean invokeCommands(List<ParsedCommand> commands) throws ParserException {
        for (ParsedCommand command : commands) {
            if (command.getCommandName().equals("exit")) {
                ArgumentsUtils.assertNoArgs(command);
                currentTable.save();
                return exit();
            } else {
                currentTable.invokeCommand(command);
            }
        }
        return true;
    }
}
