package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.Utils;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.WrongTypeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.database.Serializer;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.DatabaseInterpreterState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Create extends AbstractCommand {

    public Create(DatabaseInterpreterState state) {
        super("create", 2, state);
    }

    @Override
    public void exec(String[] param) {
        if (param[1].length() < 3 || param[1].charAt(0) != '(' || param[1].charAt(param[1].length() - 1) != ')') {
            throw new WrongTypeException("wrong signature format: " + param[1]);
        }

        String signature = param[1].substring(1, param[1].length() - 1);
        String[] types = Utils.findAll("\\S+", signature);
        List<Class<?>> typesList = new ArrayList<>();
        for (String entry : types) {
            Class<?> type = Serializer.SUPPORTED_TYPES.get(entry);
            if (type == null) {
                throw new WrongTypeException("Type <" + entry + "> is not supported");
            }
            typesList.add(Serializer.SUPPORTED_TYPES.get(entry));
        }
        if (typesList.size() == 0) {
            throw new WrongTypeException("Empty signature");
        }

        Table table;
        try {
            table = state.getDatabase().createTable(param[0], typesList);
        } catch (IOException | ColumnFormatException e) {
            throw new InputMistakeException("Cannot create table: " + e.getMessage());
        }
        if (table != null) {
            state.getOutputStream().println("created");
        } else {
            state.getOutputStream().println(param[0] + " exists");
        }
    }
}
