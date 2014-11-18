/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.database;

import ru.fizteh.fivt.storage.strings.Table;

/**
 *
 * @author shakarim
 */
public class OneTableBase {

    private Table activeTable;

    public void setActiveTable(Table table) {
        this.activeTable = table;
    }

    public Table getActiveTable() {
        return activeTable;
    }
}
