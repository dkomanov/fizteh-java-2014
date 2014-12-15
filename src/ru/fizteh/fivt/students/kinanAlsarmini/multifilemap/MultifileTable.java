package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap;

import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.SimpleTableBuilder;
import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.StringTable;

import java.io.File;
import java.io.IOException;

public class MultifileTable extends StringTable {
    public MultifileTable(String directory, String tableName) {
        super(directory, tableName);

        try {
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid file format");
        }
    }

    protected void save() throws IOException {
        DistributedSaver.save(new SimpleTableBuilder(this));
    }

    protected void load() throws IOException {
        DistributedLoader.load(new SimpleTableBuilder(this));
    }

    private File getTableDirectory() {
        File tableDirectory = new File(getDirectory(), getName());

        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
        }

        return tableDirectory;
    }
}
