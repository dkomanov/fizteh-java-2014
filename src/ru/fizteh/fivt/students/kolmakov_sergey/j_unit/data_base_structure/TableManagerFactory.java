package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure;

public class TableManagerFactory implements TableProviderFactory {
    public TableManagerFactory() {
        //Do nothing, only for implemented interface.
    }

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new TableManager(dir);
    }
}
