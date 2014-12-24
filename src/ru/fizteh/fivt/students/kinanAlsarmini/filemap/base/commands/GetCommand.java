package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

import java.util.ArrayList;

public class GetCommand<Table, Key, Value, State extends BaseFileMapShellState<Table, Key, Value>>
                                                    extends AbstractCommand<State> {
    public GetCommand() {
        super("get", "get <key>");
    }

    public void executeCommand(String params, State state) {
        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() < 1) {
            throw new IllegalArgumentException("too few arguments");
        }

        Key key = state.parseKey(parameters.get(0));
        Value value = state.get(key);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(state.valueToString(value));
        }
    }
}
