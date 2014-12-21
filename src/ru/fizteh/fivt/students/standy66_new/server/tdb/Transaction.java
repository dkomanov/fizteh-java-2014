package ru.fizteh.fivt.students.standy66_new.server.tdb;

import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableRow;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.TableSignature;

import java.io.IOException;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public interface Transaction {
    int commit() throws IOException;
    int rollback();
    TableRow get(String key);
    TableRow put(String key, TableRow value);
    TableRow remove(String key);
    TableSignature getSignature();
    int size();
}
