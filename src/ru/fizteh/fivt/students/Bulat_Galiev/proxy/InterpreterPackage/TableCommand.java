package ru.fizteh.fivt.students.Bulat_Galiev.proxy.InterpreterPackage;

import java.util.function.BiConsumer;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.Bulat_Galiev.proxy.TabledbProvider;

public class TableCommand extends Command {
    public TableCommand(final String name, final int numberOfArgs,
            final BiConsumer<TableProvider, String[]> callback) {
        super(name, numberOfArgs, callback);
    }

    @Override
    public void execute(final TableProvider connector, final String[] args)
            throws Exception {
        check(args);
        checkDataBase(connector);
        callback.accept(connector, args);
    }

    public void checkDataBase(TableProvider connector) {
        if (((TabledbProvider) connector).getDataBase() == null) {
            throw new StopInterpretationException("no table selected");
        }
    }
}
