package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

public class RollBackCommand extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().rollBack());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
