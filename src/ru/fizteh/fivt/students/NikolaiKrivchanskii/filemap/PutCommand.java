package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;
import java.util.ArrayList;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

public class PutCommand implements Commands<FileMapShellState> {

    public String getCommandName() {
        return "put";
    }

    public int getArgumentQuantity() {
        return 2;
    }

    public void implement(String[] args, FileMapShellState state)
            throws SomethingIsWrongException {
    	if (state.table == null) {
    		throw new SomethingIsWrongException("No table chosen");
    	}
        String temp = state.table.put(args[0], args[1]);
        if (temp != null) {
                System.out.println("overwrite\n" + temp);
        } else {
            System.out.println("new");
        }
    }

}
