package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Welcome;

import java.io.PrintStream;


/**
 * Created by ВАНЯ on 20.12.2014.
 */
public class Test  extends StoreableCommand {
    public Test() {
        super("test", 0);
    }

    @Override
    public boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {
        if (!checkArguments(args.length, err)) {
            return !batchModeInInteractive;
        }
        out.println("All is well");
        return true;
    }
}
