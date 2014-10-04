package ru.fizteh.fivt.students.LebedevAleksey.FileMap;


import javafx.util.Pair;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParsedCommand;

import java.util.ArrayList;

public class Table {
    protected ArrayList<TablePart> parts;

    public Table() {
        parts = new ArrayList<>();
        parts.add(new TablePart());
    }


    public TablePart getPartForKey(String key) throws LoadOrSaveError {
        TablePart tablePart = selectPartForKey(key);
        tablePart.load();
        return tablePart;
    }

    protected TablePart selectPartForKey(String key) throws LoadOrSaveError {
        return parts.get(0);
    }

    public void save() throws LoadOrSaveError {
        for (TablePart part : parts) {
            if (part.isLoaded()) {
                part.save();
            }
        }
    }

    public void invokeCommand(ParsedCommand command) throws DatabaseException {
        switch (command.getCommandName()) {
            case "put":
                Pair<String, String> args = ArgumentsUtils.get2Args(command);
                put(args.getKey(), args.getValue());
                break;
            case "get":
                get(ArgumentsUtils.get1Args(command));
                break;
            case "remove":
                remove(ArgumentsUtils.get1Args(command));
                break;
            case "list":
                ArgumentsUtils.assertNoArgs(command);
                list();
                break;
            default:
                throw new UnknownCommand();
        }
    }

    private void list() throws LoadOrSaveError {
        ArrayList<String> result = getDataForList();
        for (int i = 0; i < result.size(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(result.get(i));
        }
        System.out.println();
    }

    private ArrayList<String> getDataForList() throws LoadOrSaveError {
        ArrayList<String> result = new ArrayList<>();
        for (TablePart part : parts) {
            part.load();
            result.addAll(part.list());
        }
        return result;
    }

    private void remove(String key) throws LoadOrSaveError {
        getPartForKey(key).remove(key);
    }

    private void get(String key) throws LoadOrSaveError {
        getPartForKey(key).get(key);
    }

    private void put(String key, String value) throws LoadOrSaveError {
        getPartForKey(key).put(key, value);
    }
}
