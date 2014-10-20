package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import java.util.Set;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

public class ListCommand implements Commands<FileMapShellState> {
    
    public String getCommandName() {
        return "list";
    }

    public int getArgumentQuantity() {
        return 0;
    }

    public void implement(String[] args, FileMapShellState state)
            throws SomethingIsWrongException {
        if (state.table == null) {
            throw new SomethingIsWrongException("Table not found.");
        }
        MyTable temp = (MyTable) state.table;
        Set<String> keySet = temp.list();
        if (keySet.size() == 0) {
            System.out.println("\n");
            return;
        }
        StringBuilder sb = new StringBuilder("");
        for(String key : keySet) {
            sb.append(key);
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        System.out.println(sb.toString());
    }

}
