package ru.fizteh.fivt.students.Volodin_Denis.JUnit.main;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.TableProvider;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.TableProviderFactory;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.database.TableProviderFactoryByVolodden;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.CommandHandler;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.Interpreter;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandProviderCreate;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandProviderDrop;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandProviderUse;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandTableCommit;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandTableGet;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandTableList;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandTablePut;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandTableRemove;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands.CommandTableSize;

public class JUnit {
    
    public static void main(final String[] args) {
        try {
            TableProviderFactory tpf = new TableProviderFactoryByVolodden();
            String dir = System.getProperty("fizteh.db.dir");
            TableProvider tableProvider = tpf.create(dir);
            InterpreterState interpreterState = new StringInterpreterState(tableProvider);
            new Interpreter(interpreterState,
                    new CommandHandler[] {
                        new CommandTableGet().create(),
                        new CommandTableList().create(),
                        new CommandTablePut().create(),
                        new CommandTableRemove().create(),
                        new CommandTableSize().create(),
                        new CommandTableCommit().create(),
                        new CommandProviderCreate().create(),
                        new CommandProviderDrop().create(),
                        new CommandTableCommit().create(),
                        new CommandProviderUse().create()})
            .run(args);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.exit(ReturnCodes.ERROR);
        }
        System.exit(ReturnCodes.SUCCESS);
    }
}
