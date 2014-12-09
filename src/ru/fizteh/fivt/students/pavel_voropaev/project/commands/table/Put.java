package ru.fizteh.fivt.students.pavel_voropaev.project.commands.table;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.pavel_voropaev.project.Parser;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.WrongTypeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.Serializer;

import java.io.PrintStream;
import java.text.ParseException;

public class Put extends TableAbstractCommand {

    public Put(DatabaseInterpreterState state) {
        super("put", 2, state);
    }

    @Override
    public void exec(String[] param) {
        isTableAvailable();

        TableProvider provider = state.getDatabase();
        Table table = state.getActiveTable();
        PrintStream out = state.getOutputStream();

        param[1] = param[1].trim();
        int length = param[1].length();
        if (length < 3) {
            throw new WrongTypeException("list of values expected");
        }
        if (param[1].charAt(0) != '(') {
            throw new WrongTypeException("No ( found");
        }
        if (param[1].charAt(length - 1) != ')') {
            throw new WrongTypeException("No ) found");
        }
        String parameter = param[1].substring(1, length - 1); // Delete brackets
        Parser parser = new Parser(parameter, ' ', '\"');
        Storeable value = provider.createFor(table);

        try {
            Serializer.deserialize(table, value, parser);
        } catch (ParseException e) {
            throw new WrongTypeException(e.getMessage());
        }

        value = table.put(param[0], value);
        if (value == null) {
            out.println("new");
        } else {
            out.println("overwrite");
            out.println('(' + Serializer.serialize(table, value, ' ', '\"') + ')');
        }
    }
}
