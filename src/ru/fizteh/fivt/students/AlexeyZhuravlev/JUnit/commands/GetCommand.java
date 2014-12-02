package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class GetCommand extends JCommand {

    String key;

    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            String result = base.getUsing().get(key);
            if (result == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(result);
            }
        }
    }

    protected final void putArguments(String[] args) {
        key = args[1];
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }
}
