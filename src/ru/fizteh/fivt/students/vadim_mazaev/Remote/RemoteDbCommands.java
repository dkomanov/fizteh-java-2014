package ru.fizteh.fivt.students.vadim_mazaev.Remote;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.Helper;
import ru.fizteh.fivt.students.vadim_mazaev.Interpreter.Command;
import ru.fizteh.fivt.students.vadim_mazaev.Interpreter.Interpreter;
import ru.fizteh.fivt.students.vadim_mazaev.Interpreter.StopLineInterpretationException;

public class RemoteDbCommands {

    public static Command[] getCommands() {
        return new Command[] {
                PUT, GET, REMOVE, LIST, SIZE, COMMIT, ROLLBACK, CREATE, USE, DROP, SHOW};
    }

    private static Command PUT = new Command("put", 2, (state, args) -> {
        ClientDbState clientDbState = (ClientDbState) state;
        TableProvider manager = clientDbState.getManager();
        Table link = clientDbState.getUsedTable();
        if (link != null) {
            try {
                Storeable oldValue = link.put(args[0], manager.deserialize(link, args[1]));
                if (oldValue != null) {
                    clientDbState.getPrintStream().println("overwrite");
                    clientDbState.getPrintStream().println(manager.serialize(link, oldValue));
                } else {
                    clientDbState.getPrintStream().println("new");
                }
            } catch (ColumnFormatException | ParseException e) {
                throw new StopLineInterpretationException("wrong type (" + e.getMessage() + ")");
            }
        } else {
            throw new StopLineInterpretationException("no table");
        }
    });
    private static Command GET = new Command("get", 1, (state, args) -> {
        ClientDbState clientDbState = (ClientDbState) state;
        TableProvider manager = clientDbState.getManager();
        Table link = clientDbState.getUsedTable();
        if (link != null) {
            Storeable value = link.get(args[0]);
            if (value != null) {
                clientDbState.getPrintStream().println("found");
                clientDbState.getPrintStream().println(manager.serialize(link, value));
            } else {
                clientDbState.getPrintStream().println("not found");
            }
        } else {
            throw new StopLineInterpretationException("no table");
        }
    });
    private static Command REMOVE = new Command("remove", 1, (state, args) -> {
        ClientDbState clientDbState = (ClientDbState) state;
        Table link = clientDbState.getUsedTable();
        if (link != null) {
            Storeable removedValue = link.remove(args[0]);
            if (removedValue != null) {
                clientDbState.getPrintStream().println("removed");
            } else {
                clientDbState.getPrintStream().println("not found");
            }
        } else {
            throw new StopLineInterpretationException("no table");
        }
    });
    private static Command LIST = new Command("list", 0, (state, args) -> {
        ClientDbState clientDbState = (ClientDbState) state;
        Table link = clientDbState.getUsedTable();
        if (link != null) {
            clientDbState.getPrintStream().println(String.join(", ", link.list()));
        } else {
            throw new StopLineInterpretationException("no table");
        }
    });
    private static Command SIZE = new Command("size", 0, (state, args) -> {
        ClientDbState clientDbState = (ClientDbState) state;
        Table link = clientDbState.getUsedTable();
        if (link != null) {
            clientDbState.getPrintStream().println(link.size());
        } else {
            throw new StopLineInterpretationException("no table");
        }
    });
    private static Command COMMIT = new Command("commit", 0, (state, args) -> {
        ClientDbState clientDbState = (ClientDbState) state;
        Table link = clientDbState.getUsedTable();
        if (link != null) {
            try {
                clientDbState.getPrintStream().println(link.commit());
            } catch (IOException e) {
                throw new StopLineInterpretationException(e.getMessage());
            }
        } else {
            throw new StopLineInterpretationException("no table");
        }
    });
    private static Command ROLLBACK = new Command("rollback", 0, (state, args) -> {
        ClientDbState clientDbState = (ClientDbState) state;
        Table link = clientDbState.getUsedTable();
        if (link != null) {
            clientDbState.getPrintStream().println(link.rollback());
        } else {
            throw new StopLineInterpretationException("no table");
        }
    });
    private static Command CREATE = new Command("create", 2, (state, args) -> {
        String[] types = args[1].substring(1, args[1].length() - 1).split(" ");
        List<Class<?>> typesList = new ArrayList<>();
        for (String type : types) {
            Class<?> typeClass = Helper.SUPPORTED_NAMES_TO_TYPES.get(type);
            if (typeClass != null) {
                typesList.add(typeClass);
            } else {
                throw new StopLineInterpretationException("wrong type (" + "unsupported type: '" + type + "')");
            }
        }
        ClientDbState clientDbState = (ClientDbState) state;
        TableProvider manager = clientDbState.getManager();
        try {
            if (manager.createTable(args[0], typesList) != null) {
                clientDbState.getPrintStream().println("created");
            } else {
                throw new StopLineInterpretationException(args[0] + " exists");
            }
        } catch (IOException e) {
            throw new StopLineInterpretationException(e.getMessage());
        }
    });
    private static Command USE = new Command("use", 1, (state, args) -> {
        ClientDbState clientDbState = ((ClientDbState) state);
        TableProvider manager = clientDbState.getManager();
        Table newTable = manager.getTable(args[0]);
        Table usedTable = clientDbState.getUsedTable();
        if (newTable != null) {
            if (usedTable != null && usedTable.getNumberOfUncommittedChanges() > 0) {
                clientDbState.getPrintStream().println(usedTable.getNumberOfUncommittedChanges() + " unsaved changes");
            } else {
                clientDbState.setUsedTable(newTable);
                clientDbState.getPrintStream().println("using " + args[0]);
            }
        } else {
            throw new StopLineInterpretationException(args[0] + " not exists");
        }
    });
    private static Command DROP = new Command("drop", 1, (state, args) -> {
        ClientDbState clientDbState = ((ClientDbState) state);
        TableProvider manager = clientDbState.getManager();
        Table usedTable = clientDbState.getUsedTable();
        if (usedTable != null && usedTable.getName().equals(args[0])) {
            clientDbState.setUsedTable(null);
        }
        try {
            manager.removeTable(args[0]);
            clientDbState.getPrintStream().println("dropped");
        } catch (IllegalStateException e) {
            throw new StopLineInterpretationException("tablename not exists");
        } catch (IOException e) {
            throw new StopLineInterpretationException(e.getMessage());
        }
    });
    private static Command SHOW = new Command("show", 1, (state, args) -> {
        if (args[0].equals("tables")) {
            ClientDbState clientDbState = (ClientDbState) state;
            TableProvider manager = clientDbState.getManager();
            List<String> tableNames = manager.getTableNames();
            clientDbState.getPrintStream().println("table_name row_count");
            for (String name : tableNames) {
                Table curTable = manager.getTable(name);
                clientDbState.getPrintStream().println(curTable.getName() + " " + curTable.size());
            }
        } else {
            throw new StopLineInterpretationException(Interpreter.NO_SUCH_COMMAND_MSG + "show " + args[0]);
        }
    });
}
