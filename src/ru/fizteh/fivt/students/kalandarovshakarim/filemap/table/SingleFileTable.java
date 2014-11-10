/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.io.IOException;
import java.util.Map.Entry;

/**
 *
 * @author shakarim
 */
public class SingleFileTable extends AbstractTable {

    public SingleFileTable(String tableName) throws IOException {
        super(tableName);
        load();
    }

    private void load() throws IOException {
        try (TableReader reader = new TableReader(getName())) {
            String key;
            String value;

            while (!reader.eof()) {
                key = reader.read();
                value = reader.read();
                table.put(key, value);
            }
        }
    }

    @Override
    protected void save() throws IOException {
        try (TableWriter writer = new TableWriter(getName())) {
            long fileLen = 0;
            for (Entry<String, String> entry : table.entrySet()) {
                fileLen += writer.write(entry.getKey());
                fileLen += writer.write(entry.getValue());
            }
            writer.setLength(fileLen);
        }
    }
}
