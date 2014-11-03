package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import java.io.PrintStream;

/**
 * @author AlexeyZhuravlev
 */
public class CommitCommand extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            PrintStream out = System.out;
            System.setOut(new PrintStream(new DummyOutputStream()));
            int num = base.getUsing().commit();
            System.setOut(out);
            System.out.println(num);
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
