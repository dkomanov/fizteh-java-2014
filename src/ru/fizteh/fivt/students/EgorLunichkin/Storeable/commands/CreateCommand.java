package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableException;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.TypeManager;

import java.io.IOException;
import java.util.List;

public class CreateCommand implements Command {
    public CreateCommand(StoreableTableProvider stp, String givenName, String[] givenTypeNames)
            throws StoreableException {
        sTableProvider = stp;
        tableName = givenName;
        StringBuilder typeNames = new StringBuilder();
        for (int ind = 0; ind < givenTypeNames.length; ++ind) {
            if (ind == 0) {
                if (!givenTypeNames[ind].startsWith("(")) {
                    throw new StoreableException("Typename list must be in brackets");
                } else {
                    typeNames.append(givenTypeNames[ind].substring(1));
                }
            }
            if (ind == givenTypeNames.length - 1) {
                if (!givenTypeNames[ind].endsWith(")")) {
                    throw new StoreableException("Typename list must be in brackets");
                } else {
                    typeNames.append(givenTypeNames[ind].substring(0, givenTypeNames[ind].length() - 1));
                }
                continue;
            }
            typeNames.append(givenTypeNames[ind]);
        }
        types = TypeManager.getClasses(typeNames.toString());
    }

    private StoreableTableProvider sTableProvider;
    private String tableName;
    private List<Class<?>> types;

    @Override
    public void run() throws StoreableException {
        try{
            Table table = sTableProvider.createTable(tableName, types);
            if (table == null) {
                System.out.println(tableName + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IOException ex) {
            throw new StoreableException(ex.getMessage());
        }
    }
}
