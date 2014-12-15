package ru.fizteh.fivt.students.sautin1.proxy.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

/**
 * Created by sautin1 on 12/10/14.
 */
public class StoreableTableIOToolsMultipleFiles extends TableIOToolsMultipleFiles<Storeable, StoreableTable,
        StoreableTableProvider> {

    public StoreableTableIOToolsMultipleFiles(int dirQuantity, int fileQuantity,
                                              String encoding, String fileExtension, String dirExtension) {
        super(dirQuantity, fileQuantity, encoding, fileExtension, dirExtension);
    }

    public StoreableTableIOToolsMultipleFiles(int dirQuantity, int fileQuantity,
                                              String encoding) {
        super(dirQuantity, fileQuantity, encoding);
    }

    public StoreableTableIOToolsMultipleFiles() {
        super();
    }
}
