package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

import ru.fizteh.fivt.students.LebedevAleksey.Shell01.CommandParser;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.Log;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParsedCommand;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParserException;

import java.util.List;

public class FileMap extends CommandParser {
    public static void main(String[] args) {
        FileMap map = new FileMap();
        map.run(args);
        Log.closeStream();
    }

    @Override
    protected boolean invokeCommands(List<ParsedCommand> commands) throws ParserException {
        return false;
    }
}
