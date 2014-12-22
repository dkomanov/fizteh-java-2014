package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.io.IOException;
import java.text.ParseException;

public class PutCommand implements Command {
    public PutCommand() {}

    private ParallelTableProvider base;
    private String key;
    private String value;

    @Override
    public void run() throws IOException {
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
                throw new IOException("wrong type (" + ex.getMessage() + ")");
            }
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("put: Too many arguments");
        }
        if (args.length < minArguments()) {
            throw new ParallelException("put: Too few arguments");
        }
        key = args[0];
        value = args[1];
        base = ptp;
    }

    @Override
    public int minArguments() {
        return 2;
    }

    @Override
    public int maxArguments() {
        return 2;
    }
}
