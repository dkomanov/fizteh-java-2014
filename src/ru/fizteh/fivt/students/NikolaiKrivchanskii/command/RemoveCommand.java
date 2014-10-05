package ru.fizteh.fivt.students.NikolaiKrivchanskii.command;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.Commands;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.ShellState;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.SomethingIsWrongException;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.UtilMethods;

public class RemoveCommand implements Commands<ShellState>{
    
    public String getCommandName() {
        return "rm";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    
    
    private void delete(File f) throws SomethingIsWrongException {
        if (f.isDirectory()) {
            if (f.list().length == 0) {
                if(!f.delete()) {
                	throw new SomethingIsWrongException("Error occured while deleting. Whoopsie.");
                }
            } else {
                String[] files = f.list();
                for (String s : files) {
                    File fileDelete = new File(f, s);
                    delete(fileDelete);
                }
                if (f.list().length == 0) {
                    if(!f.delete()) {
                    	throw new SomethingIsWrongException("Error occured while deleting. Whoopsie.");
                    }
                }
            }
        } else {
            if(!f.delete()) {
            	throw new SomethingIsWrongException("Error occured while deleting. Whoopsie.");
            }

        }
    }
    
    public void implement(String[] args, ShellState state) throws SomethingIsWrongException {

        if (args.length != 1 && args.length != 2) {
            throw new SomethingIsWrongException("wrong ammount of args. should be called with one arg");
        } else if (args.length == 1) {
        	File fi = new File(args[0]);
            if (!fi.isAbsolute()) {
                fi = new File(state.getCurDir(), args[0]);
            }
            if (!fi.exists()) {
                throw new SomethingIsWrongException("file doesn't exist");
            }
            if (fi.isDirectory() && fi.list().length != 0) {
                throw new SomethingIsWrongException("Directory isn't empty. Use \"-r\" flag");
            } else {
                delete(fi);
            }
        } else {
        	File fi = new File(args[1]);
            if (!fi.isAbsolute()) {
                fi = new File(state.getCurDir(), args[1]);
            }
            if (!fi.exists()) {
                throw new SomethingIsWrongException("file doesn't exist");
            }
            if (args[0].equals("-r")) {
                delete(fi);
            } else {
                throw new SomethingIsWrongException("rm command: " + args[0] + "flag not supported");
            }
        }
    }
}
