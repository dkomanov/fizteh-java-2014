/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.SingleFileTable;

/**
 *
 * @author shakarim
 */
public class FileMapShellState {

    private SingleFileTable table = null;

    public FileMapShellState()
            throws FileNotFoundException, IOException {
        String fileName = System.getProperty("db.file");
        //if (fileName != null) {
        table = new SingleFileTable(fileName);
    }

    public SingleFileTable getState() {
        return table;
    }
}
