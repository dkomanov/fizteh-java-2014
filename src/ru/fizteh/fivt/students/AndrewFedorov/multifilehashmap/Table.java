package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.DBFileCorruptException;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.DatabaseException;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.Log;

/**
 * This class represents table stored in file system and parted into directories
 * and files.<br/>
 * Each part of data is read on require.
 * 
 * @author phoenix
 * 
 */
public class Table {
    /**
     * Interface for table part traversal
     * 
     * @author phoenix
     * 
     * @see Table#walkTableParts(TablePartWalker, boolean)
     */
    static interface TablePartWalker {
	/**
	 * This method is called by
	 * {@link Table#walkTableParts(TablePartWalker, boolean)} when a table
	 * part is loaded
	 * 
	 * @param fmap
	 *            table part
	 * @param hash
	 *            hash of relative filename; all keys that stored in this
	 *            table part should have the same hash as this number.
	 */
	public void visitTablePart(TablePart fmap, int hash)
		throws DatabaseException;
    }

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

    public Table(Path tableRoot) throws DatabaseException {
	this.tableName = tableRoot.getFileName().toString();
	this.tableRoot = tableRoot;
	tableParts = new HashMap<>();

	checkFSConsistency();
    }

    /**
     * Constructor for cloning. Does nothing.
     */
    private Table(Path tableRoot, String tableName) {
	this.tableRoot = tableRoot;
	this.tableName = tableName;
    }

    private void checkFSConsistency() throws DatabaseException {
	walkTableParts(new TablePartWalker() {

	    @Override
	    public void visitTablePart(TablePart fmap, int expectedHash)
		    throws DatabaseException {
		// check that proper keys are stored here
		Set<String> keySet = fmap.keySet();
		for (String key : keySet) {
		    int keyHash = getHash(key);
		    if (keyHash != expectedHash) {
			Log.log(Table.class,
				String.format(
					"key '%s' with hash %d is stored in file with hash %d",
					key, keyHash, expectedHash));
			throw new DBFileCorruptException(
				"Inproper keys are stored in some table parts");
		    }
		}
	    }
	}, true);
    }

    /**
     * Clones the whole table
     */
    @Override
    public Table clone() {
	Table cloneTable = new Table(tableRoot, tableName);
	cloneTable.tableParts = new HashMap<>(tableParts.size());

	for (Entry<Integer, TablePart> entry : tableParts.entrySet()) {
	    cloneTable.tableParts.put(entry.getKey(), entry.getValue().clone());
	}

	return cloneTable;
    }

    public String get(String key) throws DatabaseException {
	return obtainTablePart(getHash(key)).get(key);
    }

    public String getTableName() {
	return tableName;
    }

    /**
     * Collects all keys from all table parts assigned to this table.
     * 
     * @return actually an instance of {@link TreeSet} is returned with standard
     *         sort order.
     * @throws IOException
     * @throws DBFileCorruptException
     */
    public Set<String> keySet() throws DatabaseException {
	class KeySetCollector implements TablePartWalker {
	    TreeSet<String> set = new TreeSet<>();

	    @Override
	    public void visitTablePart(TablePart fmap, int hash) {
		set.addAll(fmap.keySet());
	    }
	}

	KeySetCollector collector = new KeySetCollector();
	walkTableParts(collector, true);
	return collector.set;
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
     * or loads it from file (file system is accessed only once for all times).
     * 
     * @param hash
     *            local hash code of keys that this table part contains.
     * @return
     * @throws IOException
     * @throws DBFileCorruptException
     */
    private TablePart obtainTablePart(int hash) throws DatabaseException {
	TablePart fmap = tableParts.get(hash);
	if (fmap == null) {
	    fmap = new TablePart(makeTablePartFilePath(hash));
	    tableParts.put(hash, fmap);
	    fmap.readFromFile();
	}
	return fmap;
    }

    /**
     * Saves all loaded into memory table parts to the file system.
     * 
     * @throws IOException
     *             if failed to write a table; table part persisting process
     *             stops on the first error case.
     */
    public void persistTable() throws IOException {
	for (TablePart fmap : tableParts.values()) {
	    fmap.writeToFile();
	}
    }

    public String put(String key, String value) throws DatabaseException {
	return obtainTablePart(getHash(key)).put(key, value);
    }

    public String remove(String key) throws DatabaseException {
	return obtainTablePart(getHash(key)).remove(key);
    }

    /**
     * Counts the number of the records stored in all table parts assigned to
     * this table.
     * 
     * @return
     * @throws IOException
     * @throws DBFileCorruptException
     */
    public long rowsNumber() throws DatabaseException {
	class KeySetCollector implements TablePartWalker {
	    long rowsNumber = 0L;

	    @Override
	    public void visitTablePart(TablePart fmap, int hash) {
		rowsNumber += fmap.size();
	    }
	}

	KeySetCollector collector = new KeySetCollector();
	walkTableParts(collector, true);
	return collector.rowsNumber;
    }

    /**
     * This methods walks over all table parts assigned to this table.
     * 
     * @param walker
     *            place your own table part handler.
     * @param simulateLoad
     *            if true, table parts will be loaded and unloaded from memory;<br/>
     *            if false, table parts will still take some place in memory,
     *            but accessing them will be faster.<br/>
     *            Table parts that are already loaded are used instead of
     *            reading them from FS.
     * @throws IOException
     * @throws DBFileCorruptException
     */
    protected void walkTableParts(TablePartWalker walker, boolean simulateLoad)
	    throws DatabaseException {
	for (int dir = 0; dir < DIRECTORIES_COUNT; dir++) {
	    for (int file = 0; file < FILES_COUNT; file++) {
		int hash = buildHash(dir, file);

		TablePart fmap;

		if (simulateLoad) {
		    fmap = tableParts.get(hash);
		    if (fmap == null) {
			fmap = new TablePart(makeTablePartFilePath(hash));
			fmap.readFromFile();
		    }
		} else {
		    fmap = obtainTablePart(hash);
		}

		walker.visitTablePart(fmap, hash);
	    }
	}
    }
}
