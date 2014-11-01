package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

/**
 * @author AlexeyZhuravlev
 */
public class CommitCommand extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
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
