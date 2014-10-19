package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DBFileCorruptException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;

/**
 * This class represents table stored in file system and parted into directories
 * and files.<br/>
 * Each part of data is read on require.
 * 
 * @author phoenix
 * 
 */
public class TableImpl implements ru.fizteh.fivt.storage.strings.Table {
    private final static int DIRECTORIES_COUNT = 16;
    private final static int FILES_COUNT = 16;

    /**
     * Builds table part relative filename hashcode;
     * 
     * @param directory
     *            not limited due to hashing algorithm.
     * @param file
     *            must be strictly less than {@link #FILES_COUNT}
     * @return
     */
    private static int buildHash(int directory, int file) {
	return directory * FILES_COUNT + file;
    }

    /**
     * Extracts directory hashcode from relative filename hashcode
     * 
     * @param hash
     * @return
     */
    private static int getDirectoryFromHash(int hash) {
	return hash / FILES_COUNT;
    }

    /**
     * Extracts filename hashcode from relative filename hashcode.
     * 
     * @param hash
     * @return
     */
    private static int getFileFromHash(int hash) {
	return hash % FILES_COUNT;
    }

    private static int getHash(String key) {
	byte byte0 = key.getBytes()[0];
	return buildHash(byte0 % DIRECTORIES_COUNT, (byte0 / DIRECTORIES_COUNT)
		% FILES_COUNT);
    }

    /**
     * Mapping between table parts and local hashes of keys that can be stored
     * inside them.
     * 
     * @see #getHash(String)
     */
    private HashMap<Integer, TablePart> tableParts;

    private final Path tableRoot;

    private final String tableName;

    /**
     * Constructs a new clear table.
     * @param tableRoot
     * @return
     */
    public static TableImpl createTable(Path tableRoot) {
	TableImpl table = new TableImpl(tableRoot);
	for (int dir = 0; dir < DIRECTORIES_COUNT; dir++) {
	    for (int file = 0; file < FILES_COUNT; file++) {
		int hash = buildHash(dir, file);
		table.tableParts.put(hash,
			new TablePart(table.makeTablePartFilePath(hash)));
	    }
	}
	return table;
    }

    /**
     * Constructs table by reading its data from file system.
     * @param tableRoot
     * @return
     * @throws DatabaseException
     */
    public static TableImpl getTable(Path tableRoot) throws DatabaseException {
	TableImpl table = new TableImpl(tableRoot);
	table.readFromFileSystem();
	return table;
    }

    /**
     * Constructor for cloning and safe table creation/obtaining
     * 
     * @param tableRoot
     */
    private TableImpl(Path tableRoot) {
	this.tableName = tableRoot.getFileName().toString();
	this.tableRoot = tableRoot;
	this.tableParts = new HashMap<>();
    }

    public void readFromFileSystem() throws DatabaseException {
	TableImpl thisClone = clone();
	tableParts.clear();

	try {
	    for (int dir = 0; dir < DIRECTORIES_COUNT; dir++) {
		for (int file = 0; file < FILES_COUNT; file++) {
		    int partHash = buildHash(dir, file);

		    TablePart fmap = new TablePart(
			    makeTablePartFilePath(partHash));
		    if (Files.exists(fmap.getTablePartFilePath())) {
			fmap.readFromFile();
		    }

		    // checking keys' hashes
		    Set<String> keySet = fmap.keySet();
		    for (String key : keySet) {
			int keyHash = getHash(key);
			if (keyHash != partHash) {
			    throw new DatabaseException(
				    "Some keys are stored in not proper places");
			}
		    }

		    tableParts.put(partHash, fmap);
		}
	    }
	} catch (Throwable thr) {
	    this.tableParts = thisClone.tableParts;
	    throw thr;
	}
    }

    /**
     * Clones the whole table
     */
    @Override
    protected TableImpl clone() {
	TableImpl cloneTable = new TableImpl(tableRoot);

	for (Entry<Integer, TablePart> entry : tableParts.entrySet()) {
	    cloneTable.tableParts.put(entry.getKey(), entry.getValue().clone());
	}

	return cloneTable;
    }

    @Override
    public String get(String key) {
	return obtainTablePart(key).get(key);
    }

    @Override
    public String getName() {
	return tableName;
    }

    /**
     * Collects all keys from all table parts assigned to this table.
     * 
     * @return actually an instance of {@link TreeSet} is returned with standard
     *         alphabetic sort order.
     * @throws IOException
     * @throws DBFileCorruptException
     */
    @Override
    public List<String> list() {
	List<String> keySet = new LinkedList<String>();

	for (TablePart part : tableParts.values()) {
	    keySet.addAll(part.keySet());
	}

	return keySet;
    }

    /**
     * Builds table file path from its hash that describes directory and file
     * name.
     * 
     * @param hash
     * @return
     */
    private Path makeTablePartFilePath(int hash) {
	return tableRoot.resolve(Paths.get(getDirectoryFromHash(hash) + ".dir",
		getFileFromHash(hash) + ".dat"));
    }

    /**
     * Gets {@link TablePart} instance assigned to this {@code hash} from memory
     * 
     * @param hash
     *            local hash code of keys that this table part contains.
     * @return
     * @throws IOException
     * @throws DBFileCorruptException
     */
    private TablePart obtainTablePart(String key) {
	checkKeyValidity(key);
	return tableParts.get(getHash(key));
    }

    @Override
    public String put(String key, String value) {
	return obtainTablePart(key).put(key, value);
    }

    @Override
    public String remove(String key) {
	return obtainTablePart(key).remove(key);
    }

    /**
     * Counts the number of the records stored in all table parts assigned to
     * this table.
     * 
     * @return
     * @throws IOException
     * @throws DBFileCorruptException
     */
    @Override
    public int size() {
	int rowsNumber = 0;

	for (TablePart part : tableParts.values()) {
	    rowsNumber += part.size();
	}

	return rowsNumber;
    }
    
    private void checkKeyValidity(String key) throws IllegalArgumentException {
	if (key == null) {
	    throw new IllegalArgumentException("Key must not be null");
	}
    }

    public int getUncommittedChangesCount() {
	int diffsCount = 0;
	for (TablePart part : tableParts.values()) {
	    diffsCount += part.getUncommittedChangesCount();
	}
	return diffsCount;
    }

    @Override
    public int commit() throws DatabaseException {
	int diffsCount = 0;
	for (TablePart part : tableParts.values()) {
	    diffsCount += part.commit();
	}
	return diffsCount;
    }

    @Override
    public int rollback() {
	int diffsCount = 0;
	for (TablePart part : tableParts.values()) {
	    diffsCount += part.rollback();
	}
	return diffsCount;
    }
}
