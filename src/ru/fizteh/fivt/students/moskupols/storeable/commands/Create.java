package ru.fizteh.fivt.students.moskupols.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.moskupols.cliutils.StopProcessingException;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.NameFirstCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;
import ru.fizteh.fivt.students.moskupols.storeable.StoreableAtomType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moskupols on 09.12.14.
 */
public class Create extends NameFirstCommand {
    @Override
    public String name() {
        return "create";
    }

    @Override
    public String checkArgs(String[] args) {
        String ret = super.checkArgs(args);
        if (ret == null) {
            if (args.length < 3) {
                return "At least 1 column type is expected";
            }
        }
        return ret;
    }

    @Override
    protected void performAction(Object context, String[] args)
            throws StopProcessingException, CommandExecutionException {
        final StoreableContext cont = (StoreableContext) context;
        List<Class<?>> types = new ArrayList<>(args.length - 2);
        for (int i = 2; i < args.length; i++) {
            final StoreableAtomType atomType = StoreableAtomType.withPrintedName(args[i]);
            if (atomType == null) {
                throw new CommandExecutionException(this, "Unknown type" + args[i]);
            }
            types.add(atomType.boxedClass);
        }
        Table newTable;
        try {
            newTable = cont.getProvider().createTable(args[1], types);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        if (newTable == null) {
            System.out.println(String.format("%s exists", args[1]));
        } else {
            System.out.println("created");
        }
    }
}
