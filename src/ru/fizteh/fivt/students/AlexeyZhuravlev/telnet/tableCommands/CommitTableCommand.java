package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;


import ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.AdvancedTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class CommitTableCommand extends TableCommand {

    @Override
    public void execute(AdvancedTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().commit());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
