package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;
import java.util.ArrayList;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.Commands;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.SomethingIsWrongException;

public class RemoveKeyCommand implements Commands<FileMapShellState> {

    public String getCommandName() {
        return "remove";
    }

    public int getArgumentQuantity() {
        return 1;
    }

    public void implement(String[] args, FileMapShellState state)
            throws SomethingIsWrongException {
        String a = state.table.remove(args[0]);
        if (a.isEmpty()) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + a);
        }
    }

}
