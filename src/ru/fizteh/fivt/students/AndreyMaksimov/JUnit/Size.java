package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;


public class Size extends JUnitCommand {

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().dirtyTable.recordsNumber());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}

