package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

public class ExitCommand implements Commands<FileMapShellState> {
    
    public String getCommandName() {
        return "exit";
    }

    public int getArgumentQuantity() {
        return 0;
    }
    public void implement(String[] args, FileMapShellState state) throws SomethingIsWrongException {
        MyTable temp = (MyTable) state.table;
        if (state.table != null && !temp.getAutoCommit()) {
            temp.rollback();
        } else if (temp.getAutoCommit() && state.table != null) {
            temp.commit();
        }
        throw new SomethingIsWrongException("EXIT");
    }

}
