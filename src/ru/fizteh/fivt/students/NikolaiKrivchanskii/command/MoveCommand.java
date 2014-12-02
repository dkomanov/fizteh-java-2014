package ru.fizteh.fivt.students.NikolaiKrivchanskii.command;
//package ru.fizteh.fivt.students.krivchansky.filemap;


import java.io.File;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.Commands;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.ShellState;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.SomethingIsWrongException;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.UtilMethods;


public class MoveCommand implements Commands<ShellState> {
    
    public String getCommandName() {
        return "mv";
    }

    public int getArgumentQuantity() {
        return 2;
    }
    
    private void shift(File from, File to) throws SomethingIsWrongException {
        if (from.isFile()) {
            CopyCommand.copy(from, to);
        } else {
            File newPlace = new File(to, from.getName());
            if (!newPlace.exists() || !newPlace.mkdir()) {
                throw new SomethingIsWrongException("Unable to create new directory " + from.getName());
            }
            for (String tmp : from.list()) {
                shift(new File(from, tmp), newPlace);
            }
            from.delete();
        }
    }
    
    
    public void implement(String[] args, ShellState state) throws SomethingIsWrongException {
        String from = args[0];
        String to = args[1];
        File source = UtilMethods.getAbsoluteName(from, state);
        File destination = UtilMethods.getAbsoluteName(to, state);
        if (!source.exists()) {
            throw new SomethingIsWrongException("The file " + from + " doesn't exist.");
        }
        if (source.getParent().equals(destination.getParent()) && destination.isDirectory()) {
            if (!source.renameTo(destination)) {
                throw new SomethingIsWrongException("Error acquired while renaming the " + from + " to " + to);
            }
            return;
        }
        if (!destination.isDirectory()) {
            if (!source.renameTo(destination)) {
                throw new SomethingIsWrongException(to + " Error acquired while renaming it.");
            }
            return;
        }
        shift(source, destination);
        
    }
}
