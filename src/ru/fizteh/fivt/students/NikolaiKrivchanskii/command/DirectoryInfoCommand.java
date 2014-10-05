package ru.fizteh.fivt.students.NikolaiKrivchanskii.command;
//package ru.fizteh.fivt.students.krivchansky.filemap;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.Commands;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.ShellState;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.UtilMethods;


public class DirectoryInfoCommand implements Commands<ShellState> {
    
    public String getCommandName() {
        return "dir";
    }

    public int getArgumentQuantity() {
        return 0;
    }
    
    public void implement(String[] args, ShellState state) {
        File dir = new File(state.getCurDir());
        String[] files = dir.list();
        List<String> tempObj = Arrays.asList(files);
        if(tempObj.size() > 0) {
            String anotherTempObj = UtilMethods.uniteItems(tempObj, "\n");
            System.out.println(anotherTempObj);
        }
    }

}
