package ru.fizteh.fivt.students.NikolaiKrivchanskii.command;
//package ru.fizteh.fivt.students.krivchansky.filemap;

import java.io.File;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.Commands;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.ShellState;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.SomethingIsWrongException;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.UtilMethods;


public class MakeDirectoryCommand implements Commands<ShellState> {
    
    public String getCommandName() {
        return "mkdir";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    public void implement (String[] args, ShellState state) throws SomethingIsWrongException{
        String nameOfDirectory = args [0];
        File creating = UtilMethods.getAbsoluteName(nameOfDirectory, state);
        if (!creating.mkdir()) {
            throw new SomethingIsWrongException("Can't make a directory " + nameOfDirectory);
        }
    }
}