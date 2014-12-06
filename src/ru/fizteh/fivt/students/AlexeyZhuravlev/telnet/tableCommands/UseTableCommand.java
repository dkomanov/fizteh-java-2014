package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

/**
 * @author AlexeyZhuravlev
 */
public class UseTableCommand extends TableCommand {

    String name;

    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        if (base.getUsing() != null && base.getUsing().getNumberOfUncommittedChanges() != 0) {
            System.out.println(base.getUsing().getNumberOfUncommittedChanges() + " unsaved changes");
        } else if (base.setUsing(name) == null) {
            System.out.println(name + " not exists");
        } else {
            System.out.println("using " + name);
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    protected void putArguments(String[] args) {
        name = args[1];
    }
}
