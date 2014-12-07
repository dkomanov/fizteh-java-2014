package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import java.io.PrintStream;

public class Commit extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            PrintStream out = System.out;
            System.setOut(new PrintStream(new ImaginaryOutputStream()));
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

