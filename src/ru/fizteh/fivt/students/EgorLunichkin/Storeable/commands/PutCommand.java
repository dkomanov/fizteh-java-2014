package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableException;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;

import java.text.ParseException;

public class PutCommand implements Command {
    public PutCommand(StoreableTableProvider stp, String givenKey, String givenValue) {
        key = givenKey;
        value = givenValue;
        sTableProvider = stp;
    }

    private StoreableTableProvider sTableProvider;
    private String key;
    private String value;

    @Override
    public void run() throws StoreableException {
        if (sTableProvider.getUsing() == null) {
            System.out.println("no table");
        } else {
            try {
                Storeable newValue = sTableProvider.deserialize(sTableProvider.getUsing(), value);
                Storeable oldValue = sTableProvider.getUsing().put(key, newValue);
                if (oldValue == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite\n" + sTableProvider.serialize(sTableProvider.getUsing(), oldValue));
                }
            } catch (ParseException ex) {
                throw new StoreableException("wrong type (" + ex.getMessage() + ")");
            }
        }
    }
}
