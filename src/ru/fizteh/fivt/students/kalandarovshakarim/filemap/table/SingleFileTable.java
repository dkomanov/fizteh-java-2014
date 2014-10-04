/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author shakarim
 */
public class SingleFileTable extends AbstractTable {

    private String fileName;

    public SingleFileTable(String fileName)
            throws FileNotFoundException, IOException {
        this.fileName = fileName;
        TableReader.loadTable(fileName, table);
    }

    public void save() throws FileNotFoundException, IOException {
        TableWriter.saveTable(fileName, table);
    }
}
