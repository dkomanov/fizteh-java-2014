package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

import java.util.ArrayList;

public class ListCommand<Table, Key, Value, State extends BaseFileMapShellState<Table, Key, Value>>
                                                    extends AbstractCommand<State> {
    public ListCommand() {
        super("list", "list");
    }

    public void executeCommand(String params, State state) {
        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 0) {
            throw new IllegalArgumentException("too many arguments");
        }

        ArrayList<Key> keys = new ArrayList<Key>(state.listKeys());

        if (keys == null) {
            System.out.println("");
        } else {
            StringBuilder keyString = new StringBuilder();

            for (Key key : keys) {
                keyString.append(state.keyToString(key) + " ");
            }

            System.out.println(keyString.toString());
        }
    }
}
