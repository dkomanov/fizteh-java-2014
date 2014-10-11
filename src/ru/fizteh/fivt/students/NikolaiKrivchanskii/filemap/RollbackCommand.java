package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

public class RollbackCommand implements Commands<FileMapShellState> {
    
    public String getCommandName() {
        return "rollback";
    }

    public int getArgumentQuantity() {
        return 0;
    }

    public void implement(String[] args, FileMapShellState state)
            throws SomethingIsWrongException {
        if (state.table == null) {
            throw new SomethingIsWrongException ("No table chosen");
        }
        state.table.rollback();
    }

}
