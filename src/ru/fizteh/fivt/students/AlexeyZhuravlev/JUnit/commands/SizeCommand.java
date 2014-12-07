package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class SizeCommand extends JCommand {
    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().size());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
