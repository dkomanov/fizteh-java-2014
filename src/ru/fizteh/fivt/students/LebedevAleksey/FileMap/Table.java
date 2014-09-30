package ru.fizteh.fivt.students.LebedevAleksey.FileMap;


import java.util.ArrayList;

public class Table {
    protected ArrayList<TablePart> parts;

    public Table() {
        parts = new ArrayList<>();
        parts.add(new TablePart());
    }


    protected TablePart getPartForKey(String key) {
        return parts.get(0);
    }

    public void Save()
    {
        for (TablePart part : parts){
            part.Save();
        }
    }


}
