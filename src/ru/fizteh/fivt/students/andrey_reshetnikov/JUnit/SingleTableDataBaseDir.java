package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

public class SingleTableDataBaseDir extends JUnitDataBaseDir {

    private HybridTable table;

    public SingleTableDataBaseDir(HybridTable passedTable) {
        table = passedTable;
    }

    public HybridTable getUsing() {
        return table;
    }
}
