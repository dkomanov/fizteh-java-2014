/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.OneTableBase;

/**
 *
 * @author shakarim
 */
public class DataBase extends OneTableBase {

    private TableProviderFactory factory;
    private TableProvider provider;

    public DataBase() {
        String pathToDb = System.getProperty("fizteh.db.dir");
        this.factory = new DataBaseProviderFactory();
        this.provider = factory.create(pathToDb);
    }

    public TableProvider getProvider() {
        return provider;
    }
}
