package ru.fizteh.fivt.students.pavel_voropaev.project.commands.table;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.Serializer;

import java.io.PrintStream;

public class Get extends TableAbstractCommand {

    public Get(DatabaseInterpreterState state) {
        super("get", 1, state);
    }

    @Override
    public void exec(String[] param) {
        isTableAvailable();

        PrintStream out = state.getOutputStream();
        Table table = state.getActiveTable();
        Storeable storeable = table.get(param[0]);

        if (storeable == null) {
            out.println("not found");
        } else {
            out.println("found");
            out.println("(" + Serializer.serialize(table, storeable, ' ', '\"') + ")");
        }
    }
}
