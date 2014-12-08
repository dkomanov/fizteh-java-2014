package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.Serializer;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;

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
