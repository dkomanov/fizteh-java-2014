package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

public class CommitCommand implements Commands<FileMapShellState>{
    
    public String getCommandName() {
        return "commit";
    }

    public int getArgumentQuantity() {
        return 0;
    }

    public void implement(String[] args, FileMapShellState state)
            throws SomethingIsWrongException {
        if (state.table == null) {
            throw new SomethingIsWrongException ("Table not found.");
        }
        state.table.commit();
    }

}
