package ru.fizteh.fivt.students.NikolaiKrivchanskii.command;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.Commands;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.ShellState;

public class WhereAreWeCommand implements Commands<ShellState> {
    
    public String getCommandName() {
        return "pwd";
    }

    public int getArgumentQuantity() {
        return 0;
    }
    
    public void implement(String[] args, ShellState state) {
        System.out.println(state.getCurDir());
    }
}
