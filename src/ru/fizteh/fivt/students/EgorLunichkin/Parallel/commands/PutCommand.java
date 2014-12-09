package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.text.ParseException;

public class PutCommand implements Command {
    public PutCommand(ParallelTableProvider stp, String givenKey, String givenValue) {
        key = givenKey;
        value = givenValue;
        base = ptp;
    }

    private ParallelTableProvider base;
    private String key;
    private String value;

    @Override
    public void run() throws ParallelException {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            try {
                Storeable newValue = base.deserialize(base.getUsing(), value);
                Storeable oldValue = base.getUsing().put(key, newValue);
                if (oldValue == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite\n" + base.serialize(base.getUsing(), oldValue));
                }
            } catch (ParseException ex) {
                throw new ParallelException("wrong type (" + ex.getMessage() + ")");
            }
        }
    }
}
