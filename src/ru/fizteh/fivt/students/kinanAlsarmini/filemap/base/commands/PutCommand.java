package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

import java.util.ArrayList;

public class PutCommand<Table, Key, Value, State extends BaseFileMapShellState<Table, Key, Value>> extends AbstractCommand<State> {
    public PutCommand() {
        super("put", "put <key> <value>");
    }

    public void executeCommand(String params, State state) {
        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 2) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() < 2) {
            throw new IllegalArgumentException("too few arguments");
        }

        Key key = state.parseKey(parameters.get(0));
        Value value = state.parseValue(parameters.get(1));
        Value oldValue = state.put(key, value);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(state.valueToString(oldValue));
        }
    }


}
