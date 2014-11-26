package ru.fizteh.fivt.students.NikolaiKrivchanskii.command;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.Commands;
//package ru.fizteh.fivt.students.krivchansky.filemap;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.ShellState;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.SomethingIsWrongException;

public class ExitCommand implements Commands<ShellState> {
    
    public String getCommandName() {
        return "exit";
    }

    public int getArgumentQuantity() {
        return 0;
    }
    public void implement(String[] args, ShellState state) throws SomethingIsWrongException {
        throw new SomethingIsWrongException("EXIT");
    }

}
