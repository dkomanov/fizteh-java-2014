package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

/**
 * @author AlexeyZhuravlev
 */
public class SingleTableDataBaseDir extends JUnitDataBaseDir {
    HybridTable table;

    public SingleTableDataBaseDir(HybridTable passedTable) {
        table = passedTable;
    }

    public HybridTable getUsing() {
        return table;
    }
}
